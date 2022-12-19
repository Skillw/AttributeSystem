package com.skillw.attsystem.internal.feature.listener.person

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.attributeSystemAPI
import com.skillw.attsystem.AttributeSystem.personalManager
import com.skillw.attsystem.api.manager.PersonalManager.Companion.pullData
import com.skillw.attsystem.api.manager.PersonalManager.Companion.pushData
import com.skillw.attsystem.internal.feature.personal.PersonalData
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync

private object Personal {


    private fun Player.init() {
        attributeSystemAPI.update(this)
        val scale = AttributeSystem.configManager.healthScale
        if (scale != -1) {
            isHealthScaled = true
            healthScale = scale.toDouble()
        } else {
            isHealthScaled = false
        }
    }

    @SubscribeEvent
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val uuid = player.uniqueId
        player.init()
        submitAsync(delay = 10) {
            player.pullData()?.register()
            if (!personalManager.hasData(player)) PersonalData(uuid).register()
        }
    }

    fun onPlayerReborn(event: PlayerRespawnEvent) {
        event.player.init()
    }


    @SubscribeEvent
    fun onPlayerLeft(event: PlayerQuitEvent) {
        val player = event.player
        val uuid = player.uniqueId
        attributeSystemAPI.remove(uuid)
        player.pushData()
    }
}
