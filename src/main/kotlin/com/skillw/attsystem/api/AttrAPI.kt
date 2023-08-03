package com.skillw.attsystem.api

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.attributeManager
import com.skillw.attsystem.AttributeSystem.attributeSystemAPI
import com.skillw.attsystem.AttributeSystem.operationManager
import com.skillw.attsystem.api.attribute.Attribute
import com.skillw.attsystem.api.attribute.compound.AttributeData
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.attsystem.api.compiled.CompiledAttrData
import com.skillw.attsystem.api.equipment.EquipmentData
import com.skillw.attsystem.api.equipment.EquipmentDataCompound
import com.skillw.attsystem.api.operation.Operation
import com.skillw.pouvoir.util.EntityUtils.livingEntity
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
     * EntityUpdate
     *
     * 更新实体(装备 属性 原版属性实现)
     *
     * 建议异步调用
     *
     * @receiver LivingEntity 实体
     */
    @JvmStatic
    fun LivingEntity.update() {
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
     * @param source String 键(源)
     * @param attributes Collection<String> 字符串集合
     * @param release Boolean 是否在下次更新属性时删除
     * @return AttributeData? 属性数据
     * @receiver LivingEntity 实体
     */
    @Deprecated(
        "Use addAttrData", ReplaceWith(
            "addCompiledData(source, attributes)",
            "com.skillw.attsystem.api.AttrAPI.addCompiledData"
        )
    )
    @JvmStatic
    fun LivingEntity.addAttribute(
        source: String,
        attributes: Collection<String>,
        release: Boolean = false,
    ): AttributeData? = addCompiledData(source, attributes)?.eval(this)?.toAttributeData()

    /**
     * 给实体添加属性数据
     *
     * @param source String 键(源)
     * @param attributeData AttributeData 属性数据
     * @param release Boolean 是否在下次更新属性时删除
     * @return AttributeData 属性数据
     * @receiver LivingEntity 实体
     */
    @Deprecated(
        "Use addAttrData",
        ReplaceWith("addAttrData(key, attributeData)", "com.skillw.attsystem.api.AttrAPI.addAttrData")
    )
    @JvmStatic
    fun LivingEntity.addAttribute(
        source: String, attributeData: AttributeData,
        release: Boolean = false,
    ): AttributeData = addAttrData(source, attributeData)

    /**
     * 给实体添加属性数据
     *
     * @param source String 键(源)
     * @param attributes Collection<String> 字符串集合
     * @param release Boolean 是否在下次更新属性时删除
     * @return AttributeData? 属性数据
     * @receiver UUID 实体uuid
     */
    @Deprecated(
        "Use addAttrData", ReplaceWith(
            "addCompiledData(source, attributes)",
            "com.skillw.attsystem.api.AttrAPI.addCompiledData"
        )
    )
    @JvmStatic
    fun UUID.addAttribute(
        source: String, attributes: Collection<String>,
        release: Boolean = false,
    ): AttributeData? = addCompiledData(source, attributes)?.eval(livingEntity())?.toAttributeData()

    /**
     * 给实体添加属性数据
     *
     * @param source String 键(源)
     * @param attributeData AttributeData 属性数据
     * @param release Boolean 是否在下次更新属性时删除
     * @return AttributeData 属性数据
     * @receiver UUID 实体uuid
     */
    @Deprecated(
        "Use addAttrData",
        ReplaceWith("addAttrData(key, attributeData)", "com.skillw.attsystem.api.AttrAPI.addAttrData")
    )
    @JvmStatic
    fun UUID.addAttribute(
        source: String, attributeData: AttributeData,
        release: Boolean = false,
    ): AttributeData = addAttrData(source, attributeData)

    /**
     * 根据 键(源) 删除实体的属性数据
     *
     * @param source String 键(源)
     * @receiver LivingEntity 实体
     */
    @Deprecated(
        "Use removeAttrData",
        ReplaceWith("removeAttrData(key)", "com.skillw.attsystem.api.AttrAPI.removeAttrData")
    )
    @JvmStatic
    fun LivingEntity.removeAttribute(source: String) = removeAttrData(source)

    /**
     * 根据 键(源) 删除实体的属性数据
     *
     * @param source String
     * @receiver UUID
     */
    @Deprecated(
        "Use removeAttrData",
        ReplaceWith("removeAttrData(key)", "com.skillw.attsystem.api.AttrAPI.removeAttrData")
    )
    @JvmStatic
    fun UUID.removeAttribute(source: String) = removeAttrData(source)

    /**
     * 给实体添加属性数据
     *
     * @param source String 键(源)
     * @param attributeData AttributeData 属性数据
     * @return AttributeData 属性数据
     * @receiver LivingEntity 实体
     */
    @JvmStatic
    fun LivingEntity.addAttrData(
        source: String, attributeData: AttributeData,
    ): AttributeData {
        return AttributeSystem.attributeDataManager.addAttrData(this, source, attributeData)
    }

    /**
     * 给实体添加属性数据
     *
     * @param source String 键(源)
     * @param attributeData AttributeData 属性数据
     * @return AttributeData 属性数据
     * @receiver UUID 实体uuid
     */
    @JvmStatic
    fun UUID.addAttrData(
        source: String, attributeData: AttributeData,
    ): AttributeData {
        return AttributeSystem.attributeDataManager.addAttrData(this, source, attributeData)
    }

    /**
     * 根据 键(源) 删除实体的属性数据
     *
     * @param source String 键(源)
     * @receiver LivingEntity 实体
     */
    @JvmStatic
    fun LivingEntity.removeAttrData(source: String) {
        AttributeSystem.attributeDataManager.removeAttrData(this, source)
    }

    /**
     * 根据 键(源) 删除实体的属性数据
     *
     * @param source String
     * @receiver UUID
     */
    @JvmStatic
    fun UUID.removeAttrData(source: String) {
        AttributeSystem.attributeDataManager.removeAttrData(this, source)
    }


    /**
     * 读取物品lore上的属性
     *
     * @param entity LivingEntity? 实体
     * @param slot String? 槽位
     * @return AttributeData? 属性数据
     * @receiver ItemStack 物品
     */
    @JvmStatic
    fun ItemStack.readItemLore(
        entity: LivingEntity? = null, slot: String? = null,
    ): CompiledAttrData? {
        return AttributeSystem.readManager.readItemLore(this, entity, slot)
    }

    /**
     * 读取物品lore上的属性
     *
     * @param entity LivingEntity? 实体
     * @param slot String? 槽位
     * @return AttributeData? 属性数据
     * @receiver Collection<ItemStack> 物品集合
     */
    @JvmStatic
    fun Collection<ItemStack>.readItemsLore(
        entity: LivingEntity? = null, slot: String? = null,
    ): CompiledAttrData {
        return AttributeSystem.readManager.readItemsLore(this, entity, slot)
    }

    /**
     * 读取物品NBT上的属性
     *
     * @param entity LivingEntity? 实体
     * @param slot String? 槽位
     * @return AttributeDataCompound? 属性数据
     * @receiver ItemStack 物品
     */
    @JvmStatic
    fun ItemStack.readItemNBT(
        entity: LivingEntity? = null, slot: String? = null,
    ): CompiledAttrData? {
        return AttributeSystem.readManager.readItemNBT(this, entity, slot)
    }

    /**
     * 读取物品NBT上的属性
     *
     * @param entity LivingEntity? 实体
     * @param slot String? 槽位
     * @return AttributeDataCompound? 属性数据
     * @receiver Collection<ItemStack> 物品集合
     */
    @JvmStatic
    fun Collection<ItemStack>.readItemsNBT(
        entity: LivingEntity? = null, slot: String? = null,
    ): CompiledAttrData {
        return AttributeSystem.readManager.readItemsNBT(this, entity, slot)
    }

    /**
     * 读取物品属性 （lore 与 nbt） 都读
     *
     * @param entity LivingEntity? 实体
     * @param slot String? 槽位
     * @return AttributeDataCompound 属性数据
     * @receiver ItemStack 物品
     */
    @JvmStatic
    fun ItemStack.readItem(
        entity: LivingEntity? = null, slot: String? = null,
    ): CompiledAttrData? {
        return AttributeSystem.readManager.readItem(this, entity, slot)
    }

    /**
     * 读取物品属性 （lore 与 nbt） 都读
     *
     * @param entity LivingEntity? 实体
     * @param slot String? 槽位
     * @return AttributeDataCompound 属性数据
     * @receiver Collection<ItemStack> 物品集合
     */
    @JvmStatic
    fun Collection<ItemStack>.readItems(
        entity: LivingEntity? = null, slot: String? = null,
    ): CompiledAttrData {
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
    fun Collection<String>.read(entity: LivingEntity? = null, slot: String? = null): CompiledAttrData? {
        return AttributeSystem.readManager.read(this, entity, slot)
    }

    /**
     * 实体是否在战斗
     *
     * @return Boolean 是否在战斗
     * @receiver LivingEntity 实体
     */
    @JvmStatic
    fun LivingEntity.isFighting(): Boolean {
        return AttributeSystem.fightStatusManager.isFighting(this)
    }

    /**
     * 让实体进入战斗状态
     *
     * @receiver LivingEntity 实体
     */
    @JvmStatic
    fun LivingEntity.intoFighting() {
        AttributeSystem.fightStatusManager.intoFighting(this)
    }

    /**
     * 让实体退出战斗状态
     *
     * @receiver LivingEntity 实体
     */

    @JvmStatic
    fun LivingEntity.outFighting() {
        AttributeSystem.fightStatusManager.outFighting(this)
    }

    /**
     * 给实体添加装备数据
     *
     * @param source 键(源)
     * @param equipments 装备数据（槽位 to 物品）
     * @return 装备数据
     * @receiver entity 实体
     */
    @JvmStatic
    @Deprecated(
        "use addEquipData",
        ReplaceWith("addEquipData(key, equipments)", "com.skillw.attsystem.api.AttrAPI.addEquipData")
    )
    fun LivingEntity.addEquipment(
        source: String,
        equipments: Map<String, ItemStack>,
    ): EquipmentData = addEquipData(source, equipments)

    /**
     * 给实体添加装备数据
     *
     * @param source 键(源)
     * @param equipmentData 装备数据
     * @return 装备数据
     * @receiver entity 实体
     */
    @JvmStatic
    @Deprecated(
        "use addEquipData",
        ReplaceWith("addEquipData(key, equipmentData)", "com.skillw.attsystem.api.AttrAPI.addEquipData")
    )
    fun LivingEntity.addEquipment(
        source: String, equipmentData: EquipmentData,
    ): EquipmentData = addEquipData(source, equipmentData)


    /**
     * 根据 键(源) 删除实体的装备数据
     *
     * @param source 键(源)
     * @return 装备数据
     * @receiver entity 实体
     */
    @JvmStatic
    @Deprecated(
        "use removeEquipData",
        ReplaceWith("removeEquipData(key)", "com.skillw.attsystem.api.AttrAPI.removeEquipData")
    )
    fun LivingEntity.removeEquipment(
        source: String,
    ) = removeEquipData(source)

    /**
     * 给实体添加装备数据
     *
     * @param source 键(源)
     * @param equipments 装备数据（槽位 to 物品）
     * @return 装备数据
     * @receiver UUID 实体的uuid
     */
    @JvmStatic
    @Deprecated(
        "use addEquipData",
        ReplaceWith("addEquipData(key, equipments)", "com.skillw.attsystem.api.AttrAPI.addEquipData")
    )
    fun UUID.addEquipment(
        source: String,
        equipments: Map<String, ItemStack>,
    ): EquipmentData = addEquipData(source, equipments)

    /**
     * 给实体添加装备数据
     *
     * @param source 键(源)
     * @param equipmentData 装备数据
     * @return 装备数据
     * @receiver UUID 实体的uuid
     */
    @JvmStatic
    @Deprecated(
        "use addEquipData",
        ReplaceWith("addEquipData(key, equipmentData)", "com.skillw.attsystem.api.AttrAPI.addEquipData")
    )
    fun UUID.addEquipment(
        source: String, equipmentData: EquipmentData,
    ): EquipmentData = addEquipData(source, equipmentData)


    /**
     * 根据 键(源) 删除实体的装备数据
     *
     * @param source 键(源)
     * @return 装备数据
     * @receiver UUID 实体的uuid
     */
    @JvmStatic
    @Deprecated(
        "use removeEquipData",
        ReplaceWith("removeEquipData(key)", "com.skillw.attsystem.api.AttrAPI.removeEquipData")
    )
    fun UUID.removeEquipment(
        source: String,
    ) = removeEquipData(source)


    /**
     * 给实体添加装备数据
     *
     * @param source 键(源)
     * @param equipments 装备数据（槽位 to 物品）
     * @return 装备数据
     * @receiver entity 实体
     */
    @JvmStatic
    fun LivingEntity.addEquipData(
        source: String,
        equipments: Map<String, ItemStack>,
    ): EquipmentData {
        return AttributeSystem.equipmentDataManager.addEquipData(this, source, equipments)
    }

    /**
     * 给实体添加装备数据
     *
     * @param source 键(源)
     * @param equipmentData 装备数据
     * @return 装备数据
     * @receiver entity 实体
     */
    @JvmStatic
    fun LivingEntity.addEquipData(
        source: String, equipmentData: EquipmentData,
    ): EquipmentData {
        return AttributeSystem.equipmentDataManager.addEquipData(this, source, equipmentData)
    }


    /**
     * 根据 键(源) 删除实体的装备数据
     *
     * @param source 键(源)
     * @return 装备数据
     * @receiver entity 实体
     */
    @JvmStatic
    fun LivingEntity.removeEquipData(
        source: String,
    ) {
        AttributeSystem.equipmentDataManager.removeEquipData(this, source)
    }

    /**
     * 给实体添加装备数据
     *
     * @param source 键(源)
     * @param equipments 装备数据（槽位 to 物品）
     * @return 装备数据
     * @receiver UUID 实体的uuid
     */
    @JvmStatic
    fun UUID.addEquipData(
        source: String,
        equipments: Map<String, ItemStack>,
    ): EquipmentData {
        return AttributeSystem.equipmentDataManager.addEquipData(this, source, equipments)
    }

    /**
     * 给实体添加装备数据
     *
     * @param source 键(源)
     * @param equipmentData 装备数据
     * @return 装备数据
     * @receiver UUID 实体的uuid
     */
    @JvmStatic
    fun UUID.addEquipData(
        source: String, equipmentData: EquipmentData,
    ): EquipmentData {
        return AttributeSystem.equipmentDataManager.addEquipData(this, source, equipmentData)
    }


    /**
     * 根据 键(源) 删除实体的装备数据
     *
     * @param source 键(源)
     * @return 装备数据
     * @receiver UUID 实体的uuid
     */
    @JvmStatic
    fun UUID.removeEquipData(
        source: String,
    ) {
        AttributeSystem.equipmentDataManager.removeEquipData(this, source)
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
        AttributeSystem.realizeManager.realize(this)
    }

    /**
     * 给实体添加预编译属性数据
     *
     * @param source 源
     * @param attributes 字符串集(会据此读取出预编译属性数据)
     * @return 预编译属性数据
     * @receiver entity 实体
     */

    fun LivingEntity.addCompiledData(
        source: String,
        attributes: Collection<String>,
    ): CompiledAttrData? = uniqueId.addCompiledData(source, attributes)

    /**
     * 给实体添加预编译属性数据
     *
     * @param source 源
     * @param compiledData 预编译属性数据
     * @return 预编译属性数据
     * @receiver entity 实体
     */

    fun LivingEntity.addCompiledData(
        source: String, compiledData: CompiledAttrData,
    ): CompiledAttrData = uniqueId.addCompiledData(source, compiledData)

    /**
     * 给实体添加预编译属性数据
     *
     * @param source 源
     * @param attributes 字符串集(会据此读取出预编译属性数据)
     * @return 预编译属性数据
     * @receiver uuid UUID
     */

    fun UUID.addCompiledData(
        source: String, attributes: Collection<String>,
    ): CompiledAttrData? {
        return AttributeSystem.compiledAttrDataManager.addCompiledData(this, source, attributes)
    }

    /**
     * 给实体添加预编译属性数据
     *
     * @param source 源
     * @param compiledData 预编译属性数据
     * @return 预编译属性数据
     * @receiver uuid UUID
     */

    fun UUID.addCompiledData(
        source: String, compiledData: CompiledAttrData,
    ): CompiledAttrData {
        return AttributeSystem.compiledAttrDataManager.addCompiledData(this, source, compiledData)
    }


    /**
     * 根据 键(源) 删除实体的预编译属性数据
     *
     * @param source 键(源)
     * @receiver entity 实体
     */
    fun LivingEntity.removeCompiledData(source: String): CompiledAttrData? {
        return uniqueId.removeCompiledData(source)
    }

    /**
     * 根据 键(源) 删除实体的预编译属性数据
     *
     * @param source 键(源)
     * @receiver uuid UUID
     */
    fun UUID.removeCompiledData(source: String): CompiledAttrData? {
        return AttributeSystem.compiledAttrDataManager.removeCompiledData(this, source)
    }
}
