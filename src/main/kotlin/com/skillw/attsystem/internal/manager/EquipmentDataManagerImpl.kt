package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.equipmentDataManager
import com.skillw.attsystem.api.AttrAPI.updateAttr
import com.skillw.attsystem.api.equipment.EquipmentData
import com.skillw.attsystem.api.equipment.EquipmentDataCompound
import com.skillw.attsystem.api.equipment.EquipmentLoader
import com.skillw.attsystem.api.event.EquipmentUpdateEvent
import com.skillw.attsystem.api.manager.EquipmentDataManager
import com.skillw.pouvoir.util.isAlive
import com.skillw.pouvoir.util.livingEntity
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import java.util.*

object EquipmentDataManagerImpl : EquipmentDataManager() {
    override val key = "EquipmentDataManager"
    override val priority: Int = 4
    override val subPouvoir = AttributeSystem

    override fun get(key: UUID): EquipmentDataCompound? {
        return super.get(key) ?: kotlin.run { key.livingEntity()?.updateAttr(); super.get(key) }
    }

    override fun update(entity: LivingEntity): EquipmentDataCompound? {
        if (!entity.isAlive()) return null
        val uuid = entity.uniqueId
        var dataCompound =
            if (containsKey(uuid)) EquipmentDataCompound(this.map[uuid]!!) else EquipmentDataCompound()
        val post = EquipmentUpdateEvent.Post(entity, dataCompound)
        post.call()
        if (post.isCancelled) {
            return dataCompound
        }
        dataCompound = post.compound
        equipmentDataManager.register(uuid, dataCompound)
        dataCompound.remove("BASE-EQUIPMENT")

        for (loader in loaders) {
            if (!loader.filter(entity)) continue
            loader.loadEquipment(entity, dataCompound)
            break
        }
        val afterEvent = EquipmentUpdateEvent.After(entity, dataCompound)
        afterEvent.call()
        if (afterEvent.isCancelled) {
            return dataCompound
        }
        dataCompound = afterEvent.compound
        equipmentDataManager.register(uuid, dataCompound)
        return dataCompound
    }

    override fun addEquipment(entity: LivingEntity, key: String, equipments: Map<String, ItemStack>): EquipmentData {
        return addEquipment(entity.uniqueId, key, equipments)
    }

    override fun addEquipment(entity: LivingEntity, key: String, equipments: EquipmentData): EquipmentData {
        return addEquipment(entity.uniqueId, key, equipments)
    }

    override fun addEquipment(uuid: UUID, key: String, equipments: Map<String, ItemStack>): EquipmentData {
        return addEquipment(uuid, key, EquipmentData().apply { putAll(equipments) })
    }


    override fun addEquipment(uuid: UUID, key: String, equipmentData: EquipmentData): EquipmentData {
        return map.computeIfAbsent(uuid) { EquipmentDataCompound() }.let {
            it[key] = equipmentData
            equipmentData
        }
    }

    override fun removeEquipment(entity: LivingEntity, key: String) {
        removeEquipment(entity.uniqueId, key)
    }

    override fun removeEquipment(uuid: UUID, key: String) {
        get(uuid)?.remove(key)
    }

    override fun put(key: UUID, value: EquipmentDataCompound): EquipmentDataCompound? {
        return super.put(key, value)?.apply { entity = key.livingEntity() }
    }

    private val loaders = LinkedList<EquipmentLoader<in LivingEntity>>()

    override fun registerLoader(loader: EquipmentLoader<in LivingEntity>) {
        loaders += loader
        loaders.sorted()
    }
}
