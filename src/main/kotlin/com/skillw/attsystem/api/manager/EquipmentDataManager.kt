package com.skillw.attsystem.api.manager

import com.skillw.attsystem.api.equipment.EquipmentData
import com.skillw.attsystem.api.equipment.EquipmentDataCompound
import com.skillw.attsystem.api.equipment.EquipmentLoader
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.plugin.map.BaseMap
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * Equipment data manager
 *
 * @constructor Create empty Equipment data manager
 */
abstract class EquipmentDataManager : BaseMap<UUID, EquipmentDataCompound>(), Manager {
    /**
     * 更新实体装备数据集
     *
     * @param entity 实体
     * @return 装备数据集
     */
    abstract fun update(entity: LivingEntity): EquipmentDataCompound?


    /**
     * 给实体添加装备数据
     *
     * @param entity 实体
     * @param source 源
     * @param equipments 装备数据（槽位 to 物品）
     * @return 装备数据
     */
    @Deprecated("Use addEquipData", ReplaceWith("addEquipData(entity, key, equipments)"))
    fun addEquipment(
        entity: LivingEntity,
        source: String,
        equipments: Map<String, ItemStack>,
    ): EquipmentData = addEquipData(entity, source, equipments)

    /**
     * 给实体添加装备数据
     *
     * @param entity 实体
     * @param source 源
     * @param equipments 装备数据
     * @return 装备数据
     */
    @Deprecated("Use addEquipData", ReplaceWith("addEquipData(entity, key, equipments)"))
    fun addEquipment(
        entity: LivingEntity, source: String, equipments: EquipmentData,
    ): EquipmentData = addEquipData(entity, source, equipments)

    /**
     * 给实体添加装备数据
     *
     * @param uuid UUID
     * @param source 源
     * @param equipments 装备数据（槽位 to 物品）
     * @return 装备数据
     */
    @Deprecated("Use addEquipData", ReplaceWith("addEquipData(uuid, key, equipments)"))
    fun addEquipment(
        uuid: UUID, source: String, equipments: Map<String, ItemStack>,
    ): EquipmentData? = addEquipData(uuid, source, equipments)

    /**
     * 给实体添加装备数据
     *
     * @param uuid UUID
     * @param source 源
     * @param equipmentData 装备数据
     * @return 装备数据
     */
    @Deprecated("Use addEquipData", ReplaceWith("addEquipData(uuid, key, equipmentData)"))
    fun addEquipment(
        uuid: UUID, source: String, equipmentData: EquipmentData,
    ): EquipmentData? = addEquipData(uuid, source, equipmentData)

    /**
     * 根据 源 删除实体的装备数据
     *
     * @param entity 实体
     * @param source 源
     */
    @Deprecated("Use addEquipData", ReplaceWith("removeEquipData(entity, key)"))
    fun removeEquipment(entity: LivingEntity, source: String) = removeEquipData(entity, source)

    /**
     * 根据 源 删除实体的装备数据
     *
     * @param uuid UUID
     * @param source 源
     */
    @Deprecated("Use addEquipData", ReplaceWith("removeEquipData(uuid, key)"))
    fun removeEquipment(uuid: UUID, source: String) = removeEquipData(uuid, source)


    /**
     * 给实体添加装备数据
     *
     * @param entity 实体
     * @param source 源
     * @param equipments 装备数据（槽位 to 物品）
     * @return 装备数据
     */
    abstract fun addEquipData(
        entity: LivingEntity,
        source: String,
        equipments: Map<String, ItemStack>,
    ): EquipmentData

    /**
     * 给实体添加装备数据
     *
     * @param uuid UUID
     * @param source 源
     * @param equipments 装备数据（槽位 to 物品）
     * @return 装备数据
     */
    abstract fun addEquipData(
        uuid: UUID, source: String, equipments: Map<String, ItemStack>,
    ): EquipmentData?

    /**
     * 根据 源 删除实体的装备数据
     *
     * @param entity 实体
     * @param source 源
     */
    abstract fun removeEquipData(entity: LivingEntity, source: String): EquipmentData?

    /**
     * 根据 源 删除实体的装备数据
     *
     * @param uuid UUID
     * @param source 源
     */
    abstract fun removeEquipData(uuid: UUID, source: String): EquipmentData?

    /**
     * 添加物品到装备栏
     *
     * @param entity LivingEntity 实体
     * @param source String 源
     * @param slot String 槽位
     * @param itemStack ItemStack 物品
     * @return EquipmentData 添加失败返回null
     */
    abstract fun addEquipment(entity: LivingEntity, source: String, slot: String, itemStack: ItemStack): EquipmentData?

    /**
     * 添加物品到装备栏
     *
     * @param uuid UUID 实体uuid
     * @param source String 源
     * @param slot String 槽位
     * @param itemStack ItemStack 物品
     * @return EquipmentData 添加失败返回null
     */
    abstract fun addEquipData(uuid: UUID, source: String, slot: String, itemStack: ItemStack): EquipmentData?

    /**
     * 清空装备栏
     *
     * @param uuid UUID 实体uuid
     */
    abstract fun clearEquipData(uuid: UUID)

    /**
     * 清空装备栏
     *
     * @param entity LivingEntity 实体
     */
    abstract fun clearEquipData(entity: LivingEntity)

    /**
     * 清空某个源的装备栏
     *
     * @param uuid UUID
     * @param source String 源
     */
    abstract fun clearEquipData(uuid: UUID, source: String)

    /**
     * 清空某个源的装备栏
     *
     * @param entity LivingEntity
     * @param source String 源
     */
    abstract fun clearEquipData(entity: LivingEntity, source: String)

    /**
     * 删除装备栏中的某个物品
     *
     * @param entity LivingEntity
     * @param source String 源
     * @param slot String 槽位
     * @return ItemStack? 被删除的物品
     */
    abstract fun removeItem(entity: LivingEntity, source: String, slot: String): ItemStack?

    /**
     * 删除装备栏中的某个物品
     *
     * @param uuid UUID
     * @param source String 源
     * @param slot String 槽位
     * @return ItemStack? 被删除的物品
     */
    abstract fun removeItem(uuid: UUID, source: String, slot: String): ItemStack?

    /**
     * 获取在CompiledDataCompound中的 源
     *
     * @param source String? 装备栏的源
     * @param slot String? 装备栏的槽位
     * @return String 在CompiledDataCompound中的 源
     */
    abstract fun getSource(source: String? = null, slot: String? = null): String

    abstract fun registerLoader(loader: EquipmentLoader<in LivingEntity>)


    override fun get(key: UUID): EquipmentDataCompound? {
        return super.get(key)
    }
}
