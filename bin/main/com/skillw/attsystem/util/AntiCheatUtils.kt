package com.skillw.attsystem.util

import com.skillw.attsystem.internal.manager.ASConfig
import me.konsolas.aac.api.AACAPI
import me.konsolas.aac.api.AACExemption
import me.rerere.matrix.api.HackType
import me.rerere.matrix.api.MatrixAPIProvider
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object AntiCheatUtils {

    @JvmStatic
    fun bypassAntiCheat(player: Player) {
        if (ASConfig.aac) {
            Bukkit.getServicesManager().load(AACAPI::class.java)
                ?.addExemption(player, AACExemption("attribute-system"))
        }
        if (ASConfig.matrix) {
            MatrixAPIProvider.getAPI().tempBypass(player, HackType.MOVE, 10)
            MatrixAPIProvider.getAPI().tempBypass(player, HackType.HITBOX, 100)
            MatrixAPIProvider.getAPI().tempBypass(player, HackType.INTERACT, 100)
        }
    }

    @JvmStatic
    fun recoverAntiCheat(player: Player) {
        if (ASConfig.aac) {
            Bukkit.getServicesManager().load(AACAPI::class.java)?.apply {
                getExemptions(player).firstOrNull { it.reason == "attribute-system" }?.let {
                    removeExemption(player, it)
                }
            }
        }
    }
}