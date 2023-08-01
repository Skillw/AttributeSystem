package com.skillw.attsystem.api.event

import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class AttrDataInitEvent(
    val player: Player,
    val data: AttributeDataCompound,
) : BukkitProxyEvent() {
    override val allowCancelled = false
}
