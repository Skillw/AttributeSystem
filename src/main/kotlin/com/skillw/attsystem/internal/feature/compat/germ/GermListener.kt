package com.skillw.attsystem.internal.feature.compat.germ

import com.germ.germplugin.api.GermSlotAPI
import com.skillw.attsystem.api.event.EquipmentUpdateEvent
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.attsystem.internal.manager.ASConfig.germ
import org.bukkit.entity.Player
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.isNotAir

object GermListener {
    @SubscribeEvent
    fun load(event: EquipmentUpdateEvent.Post) {
        val player = event.entity
        if (!germ || player !is Player) return
        val compound = event.compound
        compound.remove("Germ-Equipment")
        val map = GermSlotAPI.getGermSlotIdentitysAndItemStacks(player, ASConfig.germSlots)
        map.filter { it.value.isNotAir() }.forEach {
            compound["Germ-Equipment", it.key] = it.value
        }
    }
}