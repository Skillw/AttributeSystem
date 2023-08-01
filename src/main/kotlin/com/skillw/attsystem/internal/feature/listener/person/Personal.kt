package com.skillw.attsystem.internal.feature.listener.person

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.attributeDataManager
import com.skillw.attsystem.AttributeSystem.attributeSystemAPI
import com.skillw.attsystem.AttributeSystem.personalManager
import com.skillw.attsystem.api.manager.PersonalManager.Companion.pullPreferenceData
import com.skillw.attsystem.api.manager.PersonalManager.Companion.pushData
import com.skillw.attsystem.internal.feature.personal.PreferenceData
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync

internal object Personal {


    internal fun Player.initScale() {
        val scale = AttributeSystem.configManager.healthScale
        if (scale != -1) {
            isHealthScaled = true
            healthScale = scale.toDouble()
        } else {
            isHealthScaled = false
        }
    }

    private fun Player.init() {
        attributeDataManager[uniqueId] = personalManager.pullInitialAttrData(this).compound
        initScale()
    }

    @SubscribeEvent
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val uuid = player.uniqueId
        player.init()
        submitAsync(delay = 10) {
            player.pullPreferenceData()?.register()
            if (!personalManager.hasPreferenceData(player)) PreferenceData(uuid).register()
        }
    }

    @SubscribeEvent
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
