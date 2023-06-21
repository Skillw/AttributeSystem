package com.skillw.attsystem.internal.feature.listener.realize

import com.skillw.attsystem.internal.manager.ASConfig
import org.bukkit.event.entity.EntityRegainHealthEvent
import taboolib.common.platform.event.SubscribeEvent

private object HealthRegain {
    @SubscribeEvent
    fun e(event: EntityRegainHealthEvent) {
        event.isCancelled = !ASConfig.isVanillaRegain
    }
}