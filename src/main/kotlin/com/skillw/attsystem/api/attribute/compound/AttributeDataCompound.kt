package com.skillw.attsystem.api.attribute.compound

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.attributeManager
import com.skillw.attsystem.api.AttrAPI
import com.skillw.attsystem.api.attribute.Attribute
import com.skillw.attsystem.api.status.Status
import com.skillw.attsystem.internal.core.read.ReadGroup
import com.skillw.pouvoir.api.PouvoirAPI.eval
import com.skillw.pouvoir.api.PouvoirAPI.placeholder
import com.skillw.pouvoir.api.map.LowerMap
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.ItemTag
import taboolib.module.nms.ItemTagData
import taboolib.module.nms.getItemTag
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Attribute data compound
 *
 * @constructor Create empty Attribute data compound
 */
class AttributeDataCompound : LowerMap<AttributeData> {
    /** Entity */
    var entity: LivingEntity? = null

    constructor()
    constructor(entity: LivingEntity?) {
        this.entity = entity
    }

    constructor(compound: AttributeDataCompound) {
        this.entity = compound.entity
        for (attKey in compound.keys) {
            val attributeData = compound[attKey] ?: continue
            this[attKey] = attributeData.clone()
        }
    }

    fun release() {
        filterValues { it.release }.keys.forEach(::remove)
    }

    /**
     * Clone 复制
     *
     * @return 属性数据集
     */
    fun clone(): AttributeDataCompound {
        return AttributeDataCompound(this)
    }

    override fun toString(): String {
        return map.toString()
    }

    /**
     * Has attribute
     *
     * @param key 属性键
     * @return 是否存在该属性
     */
    fun hasAttribute(key: String): Boolean {
        return hasAttribute(attributeManager[key] ?: return false)
    }

    /**
     * Has the data of this attribute
     *
     * @param attribute 属性
     * @return 是否存在该属性的数据
     */
    fun hasAttribute(attribute: Attribute): Boolean {
        return map.any { it.value.containsKey(attribute) }
    }

    /**
     * Register
     *
     * @param uuid 实体uuid
     */
    fun register(uuid: UUID) {
        AttributeSystem.attributeDataManager.register(uuid, this)
    }

    /**
     * Get attribute status
     *
     * @param attribute 属性
     * @return 属性状态
     */
    fun getAttributeStatus(attribute: Attribute): Status<*>? = getStatus(attribute)

    /**
     * Get attribute status
     *
     * @param attributeKey 属性键
     * @return 属性状态
     */
    fun getAttributeStatus(attributeKey: String): Status<*>? = getStatus(attributeKey)

    /**
     * Get status
     *
     * @param attribute 属性
     * @return 状态
     */
    fun getStatus(attribute: Attribute): Status<*>? {
        return this.getStatus(attribute.key)
    }

    /**
     * Get status
     *
     * @param attributeKey 属性键
     * @return 属性状态
     */
    fun getStatus(attributeKey: String): Status<*>? {
        var attributeStatus: Status<*>? = null
        for (attributeData in this.values) {
            for (attribute in attributeData.keys) {
                if (attribute.key == attributeKey) {
                    val other = attributeData[attribute.key] ?: continue
                    if (attributeStatus == null) attributeStatus = other.clone()
                    else attributeStatus.operation(other)
                }
            }
        }
        return attributeStatus
    }

    /**
     * To attribute data
     *
     * 转化为属性数据
     *
     * @return 属性数据
     */
    fun toAttributeData(): AttributeData {
        val attributeData = AttributeData()
        this.forEach {
            attributeData.operation(it.value)
        }
        return attributeData
    }

    /**
     * NumberOperation
     *
     * 运算操作
     *
     * @param attributeDataCompound 属性数据集
     * @return 属性数据集(操作后的)
     */
    fun operation(attributeDataCompound: AttributeDataCompound): AttributeDataCompound {
        attributeDataCompound.forEach { (key, attributeData) ->
            if (this.containsKey(key)) {
                this[key]!!.operation(attributeData)
            } else {
                this[key] = attributeDataCompound[key]!!
            }
        }
        return this
    }

    /**
     * Get
     *
     * 获取某个键的属性数据中某个属性的状态
     *
     * @param key 键
     * @param attribute 属性
     * @return 属性数据
     */
    operator fun get(key: String, attribute: String): Status<*>? {
        val attribute1: Attribute = attributeManager[attribute] as Attribute
        return this[key]?.get(attribute1)
    }

    /**
     * 获取某个键的属性数据中某个属性的状态
     *
     * @param key 键
     * @param attribute 属性
     * @return 属性数据
     */
    operator fun get(key: String, attribute: Attribute): Status<*>? {
        return this[key]?.get(attribute)
    }

    /**
     * To mutable map
     *
     * 转化为可变的map
     *
     * @return 可变的map
     */
    fun serialize(): MutableMap<String, Any> {
        val map = ConcurrentHashMap<String, Any>()
        for ((key, attributeData) in this) {
            map[key] = attributeData.serialize()
        }
        return map
    }

    /**
     * Save
     *
     * 以"ATTRIBUTE_DATA"为键保存到物品nbt
     *
     * @param itemStack 物品
     * @return 物品
     */
    fun saveTo(itemStack: ItemStack) {
        val tag = itemStack.getItemTag()
        tag.getOrPut("ATTRIBUTE_DATA") { ItemTag() }.asCompound().putAll(ItemTagData.toNBT(serialize()).asCompound())
        tag.saveTo(itemStack)
    }

    companion object {
        /**
         * 用于从NBT中读取属性数据集
         *
         * @param map Map<String, Any> NBT
         * @return AttributeDataCompound 属性数据集
         */
        fun fromMap(
            map: Map<String, Any>,
        ): AttributeDataCompound {
            val attributeDataCompound = AttributeDataCompound()
            for ((key, value) in map) {
                if (value !is Map<*, *>) continue
                val subTag = value as Map<String, Any>
                attributeDataCompound[key] = AttributeData.fromMap(subTag).release()
            }
            return attributeDataCompound
        }
    }

    /**
     * Get attr value
     *
     * @param attribute 属性
     * @param entity 实体
     * @param placeholder 占位符key (读取格式中的) 默认是total
     * @param T 返回类型
     * @return 返回值
     */
    fun <T> getAttrValue(
        attribute: Attribute,
        entity: LivingEntity? = null,
        placeholder: String = "total",
    ): T? {
        return attribute.readPattern.placeholder(
            placeholder,
            attribute,
            getStatus(attribute) ?: return null,
            entity
        ) as? T?
    }

    /**
     * Get attr value
     *
     * @param attribute 属性
     * @param entity 实体
     * @param placeholder 占位符key (读取格式中的) 默认是total
     * @param T 返回类型
     * @return 返回值
     */
    fun <T> getAttrValue(
        attribute: String, entity: LivingEntity? = null,
        placeholder: String = "total",
    ): T? {
        return getAttrValue(attributeManager[attribute] ?: return null, entity, placeholder)
    }

    /**
     * Get attr value
     *
     * @param attribute 属性
     * @param placeholder 占位符key (读取格式中的) 默认是total
     * @param T 返回类型
     * @return 返回值
     */
    fun <T> getAttrValue(
        attribute: Attribute,
        placeholder: String = "total",
    ): T? {
        return attribute.readPattern.placeholder(
            placeholder,
            attribute,
            getStatus(attribute) ?: return null,
            entity
        ) as? T?
    }

    /**
     * Get attr value
     *
     * @param attribute 属性
     * @param placeholder 占位符key (读取格式中的) 默认是total
     * @param T 返回类型
     * @return 返回值
     */
    fun <T> getAttrValue(
        attribute: String,
        placeholder: String = "total",
    ): T? {
        return getAttrValue(attributeManager[attribute] ?: return null, entity, placeholder)
    }

    fun init() {
        forEach { (_, data) ->
            data.filterKeys { !it.entity }
                .forEach { (key, _) ->
                    data.remove(key)
                }
        }
        entity ?: return
        mappingAttr()
    }

    fun mappingAttr() {
        println(toString())
        attributeManager.attributes.forEach { attribute ->
            with(attribute) {
                if (map.isEmpty() || !hasAttribute(this)) return@forEach
                val attData = getOrPut("MAP-ATTRIBUTE-${key}") { AttributeData().release() }
                map.forEach inner@{ (key, stringMap) ->
                    val att = AttrAPI.attribute(key) ?: return@inner
                    val read = att.readPattern
                    if (read !is ReadGroup<*>) return@inner
                    val status =
                        read.readNBT(
                            stringMap.mapValues { (_, str) ->
                                str.placeholder(
                                    this@AttributeDataCompound.entity!!,
                                    false
                                ).eval().toString()
                            },
                            att
                        )
                            ?: return@inner
                    attData.operation(att, status)
                }
            }
        }
        println(toString())
    }

}
