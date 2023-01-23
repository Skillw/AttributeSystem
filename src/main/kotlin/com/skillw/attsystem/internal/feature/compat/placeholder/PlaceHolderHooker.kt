package com.skillw.attsystem.internal.feature.compat.placeholder

import com.skillw.pouvoir.Pouvoir
import org.bukkit.entity.Player
import taboolib.platform.compat.PlaceholderExpansion

object PlaceHolderHooker : PlaceholderExpansion {

    override val identifier: String = "as"

    override fun onPlaceholderRequest(player: Player?, args: String): String {
        return Pouvoir.placeholderManager.replace(player, "%as_$args%")
    }
}
