package com.skillw.attsystem.api.equipment

import com.skillw.attsystem.AttributeSystem.equipmentDataManager
import com.skillw.pouvoir.api.map.LowerMap
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

    constructor(compound: EquipmentDataCompound) : this() {
        for (source in compound.keys) {
            val equipmentData = compound[source] ?: continue
            this[source] = equipmentData.clone(compound, source)
        }
    }

    fun hasChanged(item: ItemStack, source: String, slot: String): Boolean {
        val data = get(source) ?: return true
        return data.hasChanged(item, slot)
    }

    /**
     * Set
     *
     * @param source 键
     * @param slot 子键(槽位)
     * @param itemStack 物品
     * @return 物品
     */
    operator fun set(source: String, slot: String, itemStack: ItemStack): ItemStack {
        entity?.let { equipmentDataManager.addEquipment(it, source, slot, itemStack) }
        return itemStack
    }

    override fun put(key: String, value: EquipmentData): EquipmentData? {
        return entity?.let {
            equipmentDataManager.addEquipData(it, key, value.apply {
                compound = this@EquipmentDataCompound
                source = key
            })
        }
    }

    internal fun uncheckedPut(key: String, value: EquipmentData): EquipmentData? {
        return super.put(key, value)
    }


    override fun remove(key: String): EquipmentData? {
        return entity?.let { equipmentDataManager.removeEquipData(it, key) }?.free()
    }

    fun clear(source: String) {
        entity?.let {
            equipmentDataManager.clearEquipData(it, source)
        }
    }

    override fun clear() {
        entity?.let {
            values.forEach(EquipmentData::free)
            equipmentDataManager.clearEquipData(it)
        }
    }

    fun removeItem(source: String, slot: String): ItemStack? {
        return entity?.let { equipmentDataManager.removeItem(it, source, slot) }
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

    internal fun uncheckedClear() {
        return super.clear()
    }

    internal fun uncheckedRemove(key: String): EquipmentData? {
        return super.remove(key)
    }

    /**
     * Clone
     *
     * @return
     */
    override fun clone(): EquipmentDataCompound {
        return EquipmentDataCompound(this)
    }

}
