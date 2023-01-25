package com.skillw.attsystem.api

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.attributeManager
import com.skillw.attsystem.AttributeSystem.attributeSystemAPI
import com.skillw.attsystem.AttributeSystem.operationManager
import com.skillw.attsystem.api.attribute.Attribute
import com.skillw.attsystem.api.attribute.compound.AttributeData
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.attsystem.api.equipment.EquipmentData
import com.skillw.attsystem.api.equipment.EquipmentDataCompound
import com.skillw.attsystem.api.operation.Operation
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * Attr API
 *
 * 提供了一些拓展函数，用于快速调用API
 *
 * @constructor Create empty Attr a p i
 */

object AttrAPI {
    /**
     * 从Map中读取AttributeDataCompound
     *
     * @return AttributeDataCompound
     * @receiver Map<String, Any>
     */
    @JvmStatic
    fun Map<String, Any>.readAttDataCompound() = AttributeDataCompound.fromMap(this)

    /**
     * 从Map中读取AttributeData
     *
     * @return AttributeData
     * @receiver Map<String, Any>
     */
    @JvmStatic
    fun Map<String, Any>.readAttData() = AttributeData.fromMap(this)

    /**
     * EntityUpdate
     *
     * 更新实体(装备 属性 原版属性实现)
     *
     * 建议异步调用
     */
    @JvmStatic
    fun LivingEntity.updateAttr() {
        attributeSystemAPI.update(this)
    }

    /**
     * 获取Operation<*>对象
     *
     * @param key String Operation名称
     * @return Operation<*>?
     */
    @JvmStatic
    fun operation(key: String): Operation<*>? {
        return operationManager[key]
    }


    /**
     * 获取属性，通过属性key或属性名
     *
     * @param key String 属性key或属性名
     * @return Attribute 属性
     */
    @JvmStatic
    fun attribute(key: String): Attribute? {
        return attributeManager[key]
    }

    /**
     * 获取属性数据集
     *
     * @return AttributeDataCompound? 属性数据集
     * @receiver UUID 实体uuid
     */
    @JvmStatic
    fun UUID.getAttrData(): AttributeDataCompound? {
        return AttributeSystem.attributeDataManager[this]
    }

    /**
     * 获取属性数据集
     *
     * @return AttributeDataCompound? 属性数据集
     * @receiver LivingEntity 实体
     */
    @JvmStatic
    fun LivingEntity.getAttrData(): AttributeDataCompound? {
        return uniqueId.getAttrData()
    }

    /**
     * 获取装备数据集
     *
     * @return EquipmentDataCompound? 装备数据集
     * @receiver UUID 实体uuid
     */
    @JvmStatic
    fun UUID.getEquipData(): EquipmentDataCompound? {
        return AttributeSystem.equipmentDataManager[this]
    }

    /**
     * 获取装备数据集
     *
     * @return EquipmentDataCompound? 装备数据集
     * @receiver LivingEntity 实体
     */
    @JvmStatic
    fun LivingEntity.getEquipData(): EquipmentDataCompound? {
        return uniqueId.getEquipData()
    }

    /**
     * 给实体添加属性数据
     *
     * @param key String 键(源)
     * @param attributes Collection<String> 字符串集合
     * @param release Boolean 是否在下次更新属性时删除
     * @return AttributeData? 属性数据
     * @receiver LivingEntity 实体
     */
    @JvmStatic
    fun LivingEntity.addAttribute(
        key: String,
        attributes: Collection<String>,
        release: Boolean = false,
    ): AttributeData? {
        return AttributeSystem.attributeDataManager.addAttribute(this, key, attributes, release)
    }

    /**
     * 给实体添加属性数据
     *
     * @param key String 键(源)
     * @param attributeData AttributeData 属性数据
     * @param release Boolean 是否在下次更新属性时删除
     * @return AttributeData 属性数据
     * @receiver LivingEntity 实体
     */
    @JvmStatic
    fun LivingEntity.addAttribute(
        key: String, attributeData: AttributeData,
        release: Boolean = false,
    ): AttributeData {
        return AttributeSystem.attributeDataManager.addAttribute(this, key, attributeData, release)
    }

    /**
     * 给实体添加属性数据
     *
     * @param key String 键(源)
     * @param attributeData AttributeData 属性数据
     * @param release Boolean 是否在下次更新属性时删除
     * @return AttributeData 属性数据
     * @receiver UUID 实体uuid
     */
    @JvmStatic
    fun UUID.addAttribute(
        key: String, attributeData: AttributeData,
        release: Boolean = false,
    ): AttributeData {
        return AttributeSystem.attributeDataManager.addAttribute(this, key, attributeData, release)
    }

    /**
     * 给实体删除属性数据
     *
     * @param key String 键(源)
     * @receiver LivingEntity 实体
     */
    @JvmStatic
    fun LivingEntity.removeAttribute(key: String) {
        AttributeSystem.attributeDataManager.removeAttribute(this, key)
    }

    /**
     * 给实体删除属性数据
     *
     * @param key String
     * @receiver UUID
     */
    @JvmStatic
    fun UUID.removeAttribute(key: String) {
        AttributeSystem.attributeDataManager.removeAttribute(this, key)
    }

    /**
     * 读取物品lore属性
     *
     * @param entity LivingEntity? 实体
     * @param slot String? 槽位
     * @return AttributeData? 属性数据
     * @receiver ItemStack 物品
     */
    @JvmStatic
    fun ItemStack.readItemLore(
        entity: LivingEntity? = null, slot: String? = null,
    ): AttributeData? {
        return AttributeSystem.readManager.readItemLore(this, entity, slot)
    }

    /**
     * 读取物品lore属性
     *
     * @param entity LivingEntity? 实体
     * @param slot String? 槽位
     * @return AttributeData? 属性数据
     * @receiver Collection<ItemStack> 物品集合
     */
    @JvmStatic
    fun Collection<ItemStack>.readItemsLore(
        entity: LivingEntity? = null, slot: String? = null,
    ): AttributeData? {
        return AttributeSystem.readManager.readItemsLore(this, entity, slot)
    }

    /**
     * 读取物品NBT属性
     *
     * @param entity LivingEntity? 实体
     * @param slot String? 槽位
     * @return AttributeDataCompound? 属性数据
     * @receiver ItemStack 物品
     */
    @JvmStatic
    fun ItemStack.readItemNBT(
        entity: LivingEntity? = null, slot: String? = null,
    ): AttributeDataCompound? {
        return AttributeSystem.readManager.readItemNBT(this, entity, slot)
    }

    /**
     * 读取物品NBT属性
     *
     * @param entity LivingEntity? 实体
     * @param slot String? 槽位
     * @return AttributeDataCompound? 属性数据
     * @receiver Collection<ItemStack> 物品集合
     */
    @JvmStatic
    fun Collection<ItemStack>.readItemsNBT(
        entity: LivingEntity? = null, slot: String? = null,
    ): AttributeDataCompound? {
        return AttributeSystem.readManager.readItemsNBT(this, entity, slot)
    }

    /**
     * 读取物品属性
     *
     * @param entity LivingEntity? 实体
     * @param slot String? 槽位
     * @return AttributeDataCompound 属性数据
     * @receiver ItemStack 物品
     */
    @JvmStatic
    fun ItemStack.readItem(
        entity: LivingEntity? = null, slot: String? = null,
    ): AttributeDataCompound {
        return AttributeSystem.readManager.readItem(this, entity, slot)
    }

    /**
     * 读取物品属性
     *
     * @param entity LivingEntity? 实体
     * @param slot String? 槽位
     * @return AttributeDataCompound 属性数据
     * @receiver Collection<ItemStack> 物品集合
     */
    @JvmStatic
    fun Collection<ItemStack>.readItems(
        entity: LivingEntity? = null, slot: String? = null,
    ): AttributeDataCompound {
        return AttributeSystem.readManager.readItems(this, entity, slot)
    }

    /**
     * 读取字符串集合中的属性
     *
     * @param entity LivingEntity? 实体
     * @param slot String? 槽位
     * @return AttributeData 属性数据
     * @receiver Collection<String> 字符串集合
     */
    @JvmStatic
    fun Collection<String>.read(entity: LivingEntity? = null, slot: String? = null): AttributeData {
        return AttributeSystem.readManager.read(this, entity, slot)
    }


    /**
     * Add equipment
     *
     * @param key 键(源)
     * @param equipments 装备数据（槽位 to 物品）
     * @return 装备数据
     * @receiver entity 实体
     */
    @JvmStatic
    fun LivingEntity.addEquipment(
        key: String,
        equipments: Map<String, ItemStack>,
    ): EquipmentData {
        return AttributeSystem.equipmentDataManager.addEquipment(this, key, equipments)
    }

    /**
     * Add equipment
     *
     * @param key 键(源)
     * @param equipmentData 装备数据
     * @return 装备数据
     * @receiver entity 实体
     */
    @JvmStatic
    fun LivingEntity.addEquipment(
        key: String, equipmentData: EquipmentData,
    ): EquipmentData {
        return AttributeSystem.equipmentDataManager.addEquipment(this, key, equipmentData)
    }

    /**
     * 判断实体是否有属性数据
     *
     * @return Boolean 是否有属性数据
     * @receiver LivingEntity 实体
     */
    @JvmStatic
    fun LivingEntity.hasData(): Boolean = AttributeSystem.attributeDataManager.containsKey(uniqueId)

    /**
     * 让AS重新计算并给予实体的原版属性
     *
     * @receiver LivingEntity 实体
     */
    @JvmStatic
    fun LivingEntity.realize() {
        AttributeSystem.realizerManager.realize(this)
    }

}
