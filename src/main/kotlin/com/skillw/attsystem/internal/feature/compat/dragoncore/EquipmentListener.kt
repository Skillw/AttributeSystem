package com.skillw.attsystem.internal.feature.compat.dragoncore

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.equipment.EquipmentData
import eos.moe.dragoncore.api.SlotAPI
import eos.moe.dragoncore.api.event.PlayerSlotUpdateEvent
import eos.moe.dragoncore.config.Config.slotSettings
import taboolib.common.platform.Ghost
import taboolib.common.platform.event.SubscribeEvent

object EquipmentListener {
    @Ghost
    @SubscribeEvent
    fun e(event: PlayerSlotUpdateEvent) {
        val uuid = event.player.uniqueId
        val attributeItems = SlotAPI.getCacheAllSlotItem(event.player)
        attributeItems.entries.removeIf { (key, item) ->
            !slotSettings.containsKey(
                key
            ) || !slotSettings[key]!!.isAttribute || item == null
        }
        val equipmentDataCompound = AttributeSystem.equipmentDataManager[uuid] ?: return
        val equipmentData = EquipmentData()
        equipmentData.putAll(attributeItems)
        equipmentDataCompound["Dragon-Core"] = equipmentData
        AttributeSystem.equipmentDataManager.register(uuid, equipmentDataCompound)
    }
}
