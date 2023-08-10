package com.skillw.attsystem.api.attribute.compound

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.attributeManager
import com.skillw.attsystem.api.AttrAPI.attribute
import com.skillw.attsystem.api.attribute.Attribute
import com.skillw.attsystem.api.read.status.Status
import com.skillw.pouvoir.api.plugin.map.LowerMap
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
        for (source in compound.keys) {
            val attributeData = compound[source] ?: continue
            this[source] = attributeData.clone()
        }
    }


    fun release() {
        filterValues { it.release }.keys.forEach(this::remove)
    }

    /**
     * Clone 复制
     *
     * @return 属性数据集
     */
    public override fun clone(): AttributeDataCompound {
        return AttributeDataCompound(this)
    }

    override fun toString(): String {
        return serialize().toString()
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
        return any { it.value.containsKey(attribute) }
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
        var status: Status<*>? = null
        for (attributeData in this.values) {
            if (status == null)
                status = attributeData[attribute]?.clone()
            else
                attributeData[attribute]?.let { status.operation(it) }
        }
        return status
    }

    /**
     * Get status
     *
     * @param attributeKey 属性键
     * @return 属性状态
     */
    fun getStatus(attributeKey: String): Status<*>? {
        return attribute(attributeKey)?.let { getStatus(it) }
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
            attributeData.combine(it.value)
        }
        return attributeData
    }

    /**
     * NumberOperation
     *
     * 运算操作
     *
     * @param other 属性数据集
     * @return 属性数据集(操作后的)
     */
    @Deprecated("use combine", ReplaceWith("combine(other)"))
    fun operation(other: AttributeDataCompound): AttributeDataCompound = combine(other)

    fun combine(other: AttributeDataCompound): AttributeDataCompound {
        other.forEach { (source, attributeData) ->
            combine(source, attributeData)
        }
        return this
    }

    fun combine(source: String, attributeData: AttributeData) {
        this[source]?.combine(attributeData.clone()) ?: run {
            this[source] = attributeData.clone()
        }
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
        tag.computeIfAbsent("ATTRIBUTE_DATA") { ItemTag() }.asCompound()
            .putAll(ItemTagData.toNBT(serialize()).asCompound())
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
            val total = AttributeDataCompound()
            for ((key, value) in map) {
                if (value !is Map<*, *>) continue
                val data = value as Map<String, Any>
                total[key] = AttributeData.fromMap(data)
            }
            return total
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
    }

    fun allToRelease(): AttributeDataCompound {
        values.forEach(AttributeData::release)
        return this
    }

    fun removeDeep(path: String) {
        val splits = path.split(".")
        if (splits.isEmpty()) {
            remove(path)
            return
        }
        val source = splits[0]
        if (splits.size == 1) {
            remove(source)
            return
        }
        val attKey = splits[1]
        if (splits.size == 2) {
            get(source)?.remove(attribute(attKey))
            return
        }
        var map = get(source)?.get(attribute(attKey)) as? MutableMap<String, Any>? ?: return
        for (i in 2 until splits.size) {
            val key = splits[i]
            if (i == splits.size - 1) {
                map.remove(key)
            } else {
                map = map[key] as? MutableMap<String, Any>? ?: return
            }
        }
    }


}
