package com.skillw.attsystem.internal.feature.compat.dragoncore

import com.skillw.attsystem.api.equipment.EquipmentData
import com.skillw.attsystem.api.event.EquipmentUpdateEvent
import com.skillw.attsystem.internal.feature.listener.update.Update.updateAsync
import com.skillw.attsystem.internal.manager.ASConfig.dragonCore
import eos.moe.dragoncore.api.SlotAPI
import eos.moe.dragoncore.api.event.PlayerSlotUpdateEvent
import eos.moe.dragoncore.config.Config.slotSettings
import org.bukkit.entity.Player
import taboolib.common.platform.Ghost
import taboolib.common.platform.event.SubscribeEvent

object EquipmentListener {
    @Ghost
    @SubscribeEvent(bind = "eos.moe.dragoncore.api.SlotAPI")
    fun e(event: EquipmentUpdateEvent.Pre) {
        val player = event.entity as? Player ?: return
        if (!dragonCore) return
        val attributeItems = SlotAPI.getCacheAllSlotItem(player) ?: return
        attributeItems.entries.removeIf { (key, item) ->
            !slotSettings.containsKey(
                key
            ) || !slotSettings[key]!!.isAttribute || item == null
        }
        val data = event.data
        data["Dragon-Core"] = EquipmentData().apply { putAll(attributeItems) }
    }

    @Ghost
    @SubscribeEvent
    fun e(event: PlayerSlotUpdateEvent) {
        event.player.updateAsync(2)
    }
}
