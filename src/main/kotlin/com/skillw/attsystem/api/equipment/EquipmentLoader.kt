package com.skillw.attsystem.api.equipment

import com.skillw.attsystem.AttributeSystem
import com.skillw.pouvoir.api.plugin.map.component.Registrable
import org.bukkit.entity.LivingEntity

/**
 * @className EquipmentLoader
 *
 * @author Glom
 * @date 2023/1/22 9:46 Copyright 2023 user. All rights reserved.
 */
interface EquipmentLoader<E : LivingEntity> : Registrable<String>, Comparable<EquipmentLoader<*>> {
    override val key: String
    fun filter(entity: LivingEntity): Boolean = false
    fun loadEquipment(entity: E, data: EquipmentDataCompound)

    val priority: Int

    override fun compareTo(other: EquipmentLoader<*>): Int = if (this.priority == other.priority) 0
    else if (this.priority > other.priority) 1
    else -1

    override fun register() {
        AttributeSystem.equipmentDataManager.registerLoader(this as EquipmentLoader<in LivingEntity>)
    }
}