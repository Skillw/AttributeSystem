package com.skillw.attsystem.api.attribute.compound

import com.skillw.attsystem.AttributeSystem.attributeManager
import com.skillw.attsystem.api.attribute.Attribute
import com.skillw.pouvoir.api.plugin.map.BaseMap
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.ItemTag
import taboolib.module.nms.ItemTagData
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag
import java.util.concurrent.ConcurrentHashMap


/**
 * Attribute data
 *
 * @constructor Create empty Attribute data
 */
class AttributeData : BaseMap<Attribute, GroupStatus<*>> {

    constructor()
    constructor(attributeData: AttributeData) {
        this.release = attributeData.release
        for (attribute in attributeData.keys) {
            this[attribute] = attributeData[attribute]!!.clone()
        }
    }

    /** Release */
    var release = false

    /**
     * Release
     *
     * 设置为在下次属性更新时释放
     *
     * @return 自身
     */
    fun release(): AttributeData {
        this.release = true
        return this
    }

    /**
     * Un release
     *
     * 设置为不在下次属性更新时释放
     *
     * @return 自身
     */
    fun unRelease(): AttributeData {
        this.release = false
        return this
    }

    /**
     * NumberOperation
     *
     * 作运算操作
     *
     * @param attribute 属性
     * @param status 属性状态
     * @return 自身
     */
    fun operation(attribute: Attribute, status: GroupStatus<*>): AttributeData {
        if (!this.containsKey(attribute)) {
            this.register(attribute, status)
        } else {
            this[attribute] = this[attribute]!!.operation(status)
        }
        return this
    }

    /**
     * NumberOperation
     *
     * 作运算操作
     *
     * @param others 属性数据
     * @return 自身(运算后)
     */

    @Deprecated("use combine", ReplaceWith("combine(*others)"))
    fun operation(vararg others: AttributeData): AttributeData = combine(*others)

    /**
     * NumberOperation
     *
     * 作运算操作
     *
     * @param others 属性数据
     * @return 自身(运算后)
     */
    fun combine(vararg others: AttributeData): AttributeData {
        for (attributeData in others) {
            attributeData.forEach { (attribute, attributeStatus) ->
                this.operation(attribute, attributeStatus)
            }
        }
        return this
    }

    /**
     * To compound
     *
     * 转换为属性数据集
     *
     * @param key 键
     * @return 属性数据集
     */
    fun toCompound(key: String): AttributeDataCompound {
        val compound = AttributeDataCompound()
        compound.register(key, this)
        return compound
    }


    override fun toString(): String {
        return serialize().toString()
    }

    /**
     * Get
     *
     * @param attributeKey 属性键
     * @return 属性状态
     */
    operator fun get(attributeKey: String): GroupStatus<*>? {
        return this[attributeManager[attributeKey] ?: return null]
    }

    /**
     * Clone
     *
     * 复制
     *
     * @return 属性数据
     */
    public override fun clone(): AttributeData {
        return AttributeData(this)
    }

    /**
     * To map
     *
     * 转换为map
     *
     * @return Map
     */
    fun serialize(): MutableMap<String, Any> {
        val tag = ConcurrentHashMap<String, Any>()
        for ((attribute, status) in this) {
            val value = status.serialize()
            if (value.isEmpty()) continue
            tag[attribute.key] = status.serialize()
        }
        return tag
    }

    /**
     * Save
     *
     * 以"ATTRIBUTE_DATA.键"保存到物品NBT中
     *
     * @param itemStack 物品
     * @param key 键
     * @return 物品
     */
    fun save(itemStack: ItemStack, key: String): ItemStack {
        val tag = itemStack.getItemTag()
        if (tag.containsKey("ATTRIBUTE_DATA")) {
            val compound = tag["ATTRIBUTE_DATA"]!!.asCompound()
            compound[key] = ItemTagData.toNBT(this.serialize())
            tag["ATTRIBUTE_DATA"] = compound
        } else {
            val compound = ItemTag()
            compound[key] = ItemTagData.toNBT(this.serialize())
            tag["ATTRIBUTE_DATA"] = compound
        }
        return itemStack.setItemTag(tag)
    }

    companion object {

        /**
         * 用于读取NBT
         *
         * @param map Map<String, Any> NBT
         * @return AttributeData 属性数据
         */
        @JvmStatic
        fun fromMap(map: Map<String, Any>): AttributeData =
            AttributeData().apply {
                map.forEach { (attKey, value) ->
                    val attribute = attributeManager[attKey] ?: return@forEach
                    val status =
                        attribute.readPattern.readNBT(value as Map<String, Any>, attribute) ?: return@forEach
                    register(attribute, status)
                }
            }
    }
}
