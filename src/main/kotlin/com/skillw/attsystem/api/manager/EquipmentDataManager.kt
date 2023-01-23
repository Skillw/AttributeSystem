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
    abstract fun registerLoader(loader: EquipmentLoader<in LivingEntity>)

    /**
     * 更新实体装备数据集
     *
     * @param entity 实体
     * @return 装备数据集
     */
    abstract fun update(entity: LivingEntity): EquipmentDataCompound?


    /**
     * Add equipment
     *
     * @param entity 实体
     * @param key 键(源)
     * @param equipments 装备数据（槽位 to 物品）
     * @return 装备数据
     */
    abstract fun addEquipment(
        entity: LivingEntity,
        key: String,
        equipments: Map<String, ItemStack>,
    ): EquipmentData

    /**
     * Add equipment
     *
     * @param entity 实体
     * @param key 键(源)
     * @param equipments 装备数据
     * @return 装备数据
     */
    abstract fun addEquipment(
        entity: LivingEntity, key: String, equipments: EquipmentData,
    ): EquipmentData

    /**
     * Add equipment
     *
     * @param uuid UUID
     * @param key 键(源)
     * @param equipments 装备数据（槽位 to 物品）
     * @return 装备数据
     */
    abstract fun addEquipment(
        uuid: UUID, key: String, equipments: Map<String, ItemStack>,
    ): EquipmentData

    /**
     * Add equipment
     *
     * @param uuid UUID
     * @param key 键(源)
     * @param equipmentData 装备数据
     * @return 装备数据
     */
    abstract fun addEquipment(
        uuid: UUID, key: String, equipmentData: EquipmentData,
    ): EquipmentData

    /**
     * Remove equipment
     *
     * @param entity 实体
     * @param key 键(源)
     */
    abstract fun removeEquipment(entity: LivingEntity, key: String)

    /**
     * Remove equipment
     *
     * @param uuid UUID
     * @param key 键(源)
     */
    abstract fun removeEquipment(uuid: UUID, key: String)
}
