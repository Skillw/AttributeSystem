package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.compiledAttrDataManager
import com.skillw.attsystem.AttributeSystem.equipmentDataManager
import com.skillw.attsystem.api.AttrAPI.readItem
import com.skillw.attsystem.api.AttrAPI.update
import com.skillw.attsystem.api.compiled.oper.ComplexCompiledData
import com.skillw.attsystem.api.equipment.EquipmentData
import com.skillw.attsystem.api.equipment.EquipmentDataCompound
import com.skillw.attsystem.api.event.EquipmentUpdateEvent
import com.skillw.attsystem.api.event.ItemLoadEvent
import com.skillw.attsystem.api.manager.EquipmentDataManager
import com.skillw.attsystem.util.Utils.validEntity
import com.skillw.pouvoir.util.EntityUtils.isAlive
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common5.Coerce
import taboolib.module.nms.getItemTag
import taboolib.platform.util.hasLore
import taboolib.platform.util.isAir
import java.util.*

object EquipmentDataManagerImpl : EquipmentDataManager() {
    override val key = "EquipmentDataManager"
    override val priority: Int = 4
    override val subPouvoir = AttributeSystem


    override fun getSource(source: String?, slot: String?) =
        "!!Equipment" + (if (source != null) "-$source" else "") + (if (slot != null) "-$slot" else "")

    override fun get(key: UUID): EquipmentDataCompound? {
        return super.get(key) ?: kotlin.run { key.validEntity().update(); super.get(key) }
    }

    private fun uncheckedGet(key: UUID): EquipmentDataCompound? {
        return super.get(key)
    }

    override fun register(key: UUID, value: EquipmentDataCompound): EquipmentDataCompound? {
        return super.register(key, value.apply { entity = key.validEntity() })
    }

    override fun update(entity: LivingEntity): EquipmentDataCompound? {
        if (!entity.isAlive()) return null
        val uuid = entity.uniqueId
        var data = uncheckedGet(uuid) ?: EquipmentDataCompound(entity)
        equipmentDataManager.register(uuid, data)
        val pre = EquipmentUpdateEvent.Pre(entity, data)
        pre.call()
        if (pre.isCancelled) {
            return data
        }
        data = pre.data
        if (entity is Player) {
            loadPlayer(entity, data)
        } else {
            loadEntity(entity, data)
        }
        val postEvent = EquipmentUpdateEvent.Post(entity, data)
        postEvent.call()
        if (postEvent.isCancelled) {
            return data
        }
        data = postEvent.data
        equipmentDataManager.register(uuid, data)
        return data
    }

    private fun loadEntity(entity: LivingEntity, data: EquipmentDataCompound) {
        for ((slot, equipmentType) in AttributeSystem.entitySlotManager) {
            val item: ItemStack? = equipmentType.getItem(entity)
            data.addEquipment(entity, BASE_EQUIPMENT_KEY, slot, item)
        }
    }

    private fun loadPlayer(player: Player, data: EquipmentDataCompound) {
        val inv = player.inventory
        val manager = AttributeSystem.playerSlotManager
        for (playerSlot in manager.values) {
            val equipmentType = playerSlot.bukkitEquipment
            var item: ItemStack? = null
            val slotStr = playerSlot.slot
            runCatching {
                item = if (equipmentType != null) {
                    equipmentType.getItem(player)
                } else {
                    val slot = if (slotStr == "held") player.inventory.heldItemSlot else Coerce.toInteger(slotStr)
                    inv.getItem(slot)
                }
            }
            val slot = playerSlot.key
            data.addEquipment(player, BASE_EQUIPMENT_KEY, slot, item) { itemStack ->
                playerSlot.requirements.isEmpty() || playerSlot.requirements.any { itemStack.hasLore(it) }
            }
        }
    }

    private const val BASE_EQUIPMENT_KEY = "BASE-EQUIPMENT"
    private const val IGNORE_KEY = "IGNORE_ATTRIBUTE"

    private fun EquipmentDataCompound.addEquipment(
        entity: LivingEntity,
        source: String,
        slot: String,
        item: ItemStack?,
        condition: (ItemStack) -> Boolean = { true },
    ): EquipmentData? {
        return item.run {
            if (isAir() || !condition(this)) {
                removeItem(source, slot)
                return@run null
            }
            val event = ItemLoadEvent(entity, this)
            event.call()
            if (event.isCancelled) {
                removeItem(source, slot)
                return@run null
            }
            val eventItem = event.itemStack
            if (getItemTag().containsKey(IGNORE_KEY)) {
                removeItem(source, slot)
                return@run null
            }
            if (!hasChanged(eventItem, source, slot)) return@run null
            compiledAttrDataManager.addCompiledData(
                entity.uniqueId,
                getSource(source, slot),
                eventItem.readItem(entity, slot) ?: ComplexCompiledData()
            )
            return computeIfAbsent(source) { EquipmentData(this@addEquipment, source) }.apply {
                uncheckedPut(
                    slot,
                    eventItem
                )
            }
        }
    }

    override fun addEquipment(
        entity: LivingEntity,
        source: String,
        slot: String,
        itemStack: ItemStack,
    ): EquipmentData? {
        val uuid = entity.uniqueId
        return computeIfAbsent(uuid) { EquipmentDataCompound().apply { this.entity = entity } }.addEquipment(
            entity,
            source,
            slot,
            itemStack
        )
    }

    override fun addEquipData(entity: LivingEntity, source: String, equipments: Map<String, ItemStack>): EquipmentData {
        val uuid = entity.uniqueId
        return computeIfAbsent(uuid) { EquipmentDataCompound().apply { this.entity = entity } }.let { compound ->
            compound.computeIfAbsent(source) { EquipmentData(compound, source) }.apply {
                val newKeys = equipments.keys
                compiledAttrDataManager[uuid].apply {
                    filter { it.key !in newKeys }.map { it.key }.forEach(this::remove)
                }
                equipments.forEach { (slot, item) ->
                    compound.addEquipment(entity, source, slot, item)
                }
            }
        }
    }


    override fun removeEquipData(entity: LivingEntity, source: String): EquipmentData? {
        return removeEquipData(entity.uniqueId, source)
    }

    override fun removeItem(entity: LivingEntity, source: String, slot: String): ItemStack? {
        return removeItem(entity.uniqueId, source, slot)
    }

    override fun clearEquipData(entity: LivingEntity) {
        clearEquipData(entity.uniqueId)
    }

    override fun clearEquipData(entity: LivingEntity, source: String) {
        clearEquipData(entity.uniqueId, source)
    }

    override fun addEquipData(uuid: UUID, source: String, slot: String, itemStack: ItemStack): EquipmentData? {
        return addEquipment(uuid.validEntity(), source, slot, itemStack)
    }

    override fun addEquipData(uuid: UUID, source: String, equipments: Map<String, ItemStack>): EquipmentData {
        return addEquipData(uuid.validEntity(), source, equipments)
    }

    override fun removeEquipData(uuid: UUID, source: String): EquipmentData? {
        compiledAttrDataManager.removeIfStartWith(uuid, getSource(source))
        return get(uuid)?.uncheckedRemove(source)
    }

    override fun removeItem(uuid: UUID, source: String, slot: String): ItemStack? {
        val compound = this[uuid] ?: return null
        val data = compound[source] ?: return null
        compiledAttrDataManager.removeCompiledData(uuid, getSource(source, slot))
        return data.uncheckedRemove(slot)
    }

    override fun clearEquipData(uuid: UUID, source: String) {
        compiledAttrDataManager.removeIfStartWith(uuid, getSource(source))
        get(uuid)?.get(source)?.uncheckedClear()
    }

    override fun clearEquipData(uuid: UUID) {
        compiledAttrDataManager.removeIfStartWith(uuid, getSource())
        get(uuid)?.uncheckedClear()
    }

    override fun put(key: UUID, value: EquipmentDataCompound): EquipmentDataCompound? {
        return super.put(key, value)?.apply { entity = key.validEntity() }
    }
}
