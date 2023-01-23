package com.skillw.attsystem.internal.feature.realizer

import com.skillw.attsystem.api.realizer.BaseRealizer
import com.skillw.attsystem.api.realizer.component.sub.Awakeable
import com.skillw.attsystem.api.realizer.component.sub.Switchable
import com.skillw.attsystem.api.realizer.component.sub.Valuable
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent
import taboolib.common.platform.Ghost
import taboolib.common.platform.event.SubscribeEvent

@AutoRegister
internal object HealthScaleRealizer : BaseRealizer("health-scale"), Awakeable, Switchable, Valuable {

    override val fileName: String = "options.yml"
    override val defaultEnable: Boolean
        get() = true
    override val defaultValue: String
        get() = "20"


    private fun realize(player: Player) {
        with(player) {
            if (isEnable()) {
                isHealthScaled = true
                healthScale = value(this)
            } else {
                isHealthScaled = false
            }
        }
    }

    @SubscribeEvent
    fun onPlayerJoin(event: PlayerJoinEvent) {
        realize(event.player)
    }

    @Ghost
    @SubscribeEvent
    fun onPlayerReborn(event: PlayerRespawnEvent) {
        realize(event.player)
    }

    override fun onReload() {
        Bukkit.getServer().onlinePlayers.forEach(::realize)
    }

    override fun onDisable() {
        Bukkit.getServer().onlinePlayers.forEach {
            it.isHealthScaled = false
        }
    }

}