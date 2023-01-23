package com.skillw.attsystem.api.equipment

import com.skillw.pouvoir.api.plugin.map.LowerMap
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

/**
 * Equipment data compound
 *
 * @constructor Create empty Equipment data compound
 */
class EquipmentDataCompound() : LowerMap<EquipmentData>() {
    /** Entity */
    var entity: LivingEntity? = null

    constructor(entity: LivingEntity) : this() {
        this.entity = entity
    }

    constructor(equipmentDataCompound: EquipmentDataCompound) : this() {
        entity = equipmentDataCompound.entity
        for (key in equipmentDataCompound.keys) {
            val equipmentData = equipmentDataCompound[key] ?: continue
            this[key] = equipmentData.clone()
        }
    }

    /**
     * Set
     *
     * @param key 键
     * @param subKey 子键(槽位)
     * @param itemStack 物品
     * @return 物品
     */
    operator fun set(key: String, subKey: String, itemStack: ItemStack): ItemStack {
        if (!this.containsKey(key))
            this[key] = EquipmentData()
        return this[key]?.put(subKey, itemStack) ?: return itemStack
    }

    /**
     * Get
     *
     * @param key 键
     * @param subKey 子键(槽位)
     * @return 物品
     */
    operator fun get(key: String, subKey: String): ItemStack? {
        return this[key]?.get(subKey)
    }

    /**
     * Clone
     *
     * @return
     */
    fun clone(): EquipmentDataCompound {
        return EquipmentDataCompound(this)
    }
}
