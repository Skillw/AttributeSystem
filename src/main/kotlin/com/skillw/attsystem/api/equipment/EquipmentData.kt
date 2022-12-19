package com.skillw.attsystem.api.equipment

import com.skillw.pouvoir.api.map.LowerMap
import org.bukkit.inventory.ItemStack

/**
 * Equipment data
 *
 * @constructor Create empty Equipment data
 */
class EquipmentData : LowerMap<ItemStack> {

    constructor()
    constructor(release: Boolean) {
        this.release = release
    }

    constructor(equipmentData: EquipmentData, release: Boolean) {
        this.release = release
        for (key in equipmentData.keys) {
            this[key] = equipmentData[key]!!.clone()
        }
    }

    /**
     * Clone
     *
     * @return
     */
    fun clone(): EquipmentData {
        val equipmentData = EquipmentData()
        this.forEach {
            equipmentData.put(it.key, it.value.clone())
        }
        return equipmentData
    }

    constructor(equipmentData: EquipmentData) : this(equipmentData, true)

    /** Release */
    var release = false

    /**
     * Release
     *
     * 设置为在下次更新时释放装备数据
     *
     * @return
     */
    fun release(): EquipmentData {
        this.release = true
        return this
    }

    /**
     * Un release
     *
     * 设置为不在下次更新时释放装备数据
     *
     * @return
     */
    fun unRelease(): EquipmentData {
        this.release = false
        return this
    }
}
