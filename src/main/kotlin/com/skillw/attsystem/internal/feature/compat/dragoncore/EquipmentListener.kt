package com.skillw.attsystem.internal.feature.compat.dragoncore

import com.skillw.attsystem.AttributeSystem.equipmentDataManager
import com.skillw.attsystem.api.event.EquipmentUpdateEvent
import com.skillw.attsystem.internal.feature.realizer.UpdateRealizer.updateSync
import com.skillw.attsystem.internal.manager.ASConfig.dragonCore
import eos.moe.dragoncore.api.SlotAPI
import eos.moe.dragoncore.api.event.PlayerSlotUpdateEvent
import eos.moe.dragoncore.config.Config.slotSettings
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.Ghost
import taboolib.common.platform.event.SubscribeEvent
import java.util.concurrent.ConcurrentHashMap

object EquipmentListener {
    private val cacheSlots: (Player) -> Map<String, ItemStack>? by lazy {
        runCatching {
            val method = SlotAPI::class.java.getDeclaredMethod("getCacheAllSlotItem", Player::class.java)
            return@lazy { player ->
                method.invoke(null, player) as? Map<String, ItemStack>?
            }
        }.getOrElse {
            val method = SlotAPI::class.java.getDeclaredMethod("getCacheAllSlot", String::class.java)
            return@lazy { player ->
                method.invoke(null, player.name) as? Map<String, ItemStack>?
            }
        }
    }

    @Ghost
    @SubscribeEvent(bind = "eos.moe.dragoncore.api.SlotAPI")
    fun e(event: EquipmentUpdateEvent.Pre) {
        val player = event.entity as? Player ?: return
        if (!dragonCore) return
        val cache = cacheSlots(player)
        val attributeItems = cache?.let { ConcurrentHashMap(it) } ?: return
        attributeItems.keys.filter { key ->
            !slotSettings.containsKey(key) || !slotSettings[key]!!.isAttribute
        }.forEach(attributeItems::remove)
        equipmentDataManager.addEquipData(player, "Dragon-Core", attributeItems)
    }

    @Ghost
    @SubscribeEvent
    fun e(event: PlayerSlotUpdateEvent) {
        event.player.updateSync(0)
    }
}
