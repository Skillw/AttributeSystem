package com.skillw.attsystem.internal.feature.compat.dragoncore

import com.skillw.attsystem.AttributeSystem.equipmentDataManager
import com.skillw.attsystem.api.event.EquipmentUpdateEvent
import com.skillw.attsystem.internal.feature.listener.update.Update.updateAsync
import com.skillw.attsystem.internal.manager.ASConfig.dragonCore
import eos.moe.dragoncore.api.SlotAPI
import eos.moe.dragoncore.api.event.PlayerSlotUpdateEvent
import eos.moe.dragoncore.config.Config.slotSettings
import org.bukkit.entity.Player
import taboolib.common.platform.Ghost
import taboolib.common.platform.event.SubscribeEvent
import java.util.concurrent.ConcurrentHashMap

object EquipmentListener {
    @Ghost
    @SubscribeEvent(bind = "eos.moe.dragoncore.api.SlotAPI")
    fun e(event: EquipmentUpdateEvent.Pre) {
        if (!dragonCore) return
        val player = event.entity as? Player ?: return
        val attributeItems = SlotAPI.getCacheAllSlotItem(player)?.let { ConcurrentHashMap(it) } ?: return
        attributeItems.keys.filter { key ->
            !slotSettings.containsKey(key) || !slotSettings[key]!!.isAttribute
        }.forEach(attributeItems::remove)
        equipmentDataManager.addEquipData(player, "Dragon-Core", attributeItems)
    }

    @Ghost
    @SubscribeEvent
    fun e(event: PlayerSlotUpdateEvent) {
        event.player.updateAsync(2)
    }
}
