package com.skillw.attsystem.internal.feature.personal

import com.skillw.attsystem.internal.feature.personal.InitialAttrData.Companion.pushAttrData
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent

private object SaveAttrData {
    @SubscribeEvent
    fun exit(event: PlayerQuitEvent) {
        val player = event.player
        val uuid = player.uniqueId
        pushAttrData(player)
    }
}