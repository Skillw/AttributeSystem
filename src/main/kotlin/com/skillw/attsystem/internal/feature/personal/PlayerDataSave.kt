package com.skillw.attsystem.internal.feature.personal

import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent

private object PlayerUpdate {

    @SubscribeEvent(EventPriority.LOWEST)
    fun quit(event: PlayerQuitEvent) {
        val player = event.player
        InitialAttrData.pushAttrData(player)
    }
}