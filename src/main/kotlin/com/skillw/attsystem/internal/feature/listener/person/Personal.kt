package com.skillw.attsystem.internal.feature.listener.person

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.attributeSystemAPI
import com.skillw.attsystem.AttributeSystem.personalManager
import com.skillw.attsystem.api.manager.PersonalManager.Companion.pullData
import com.skillw.attsystem.api.manager.PersonalManager.Companion.pushData
import com.skillw.attsystem.internal.feature.personal.PersonalData
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.submitAsync

private object Personal {


    @SubscribeEvent
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        submitAsync(delay = 10) {
            val uuid = player.uniqueId
            player.pullData()?.register()
            if (!personalManager.hasData(player)) PersonalData(uuid).register()
            attributeSystemAPI.update(player)
        }
        submit(delay = 10) {
            val scale = AttributeSystem.configManager.healthScale
            if (scale != -1) {
                player.isHealthScaled = true
                player.healthScale = scale.toDouble()
            } else {
                player.isHealthScaled = false
            }
        }

    }


    @SubscribeEvent
    fun onPlayerLeft(event: PlayerQuitEvent) {
        val player = event.player
        val uuid = player.uniqueId
        attributeSystemAPI.remove(uuid)
        player.pushData()
    }
}
