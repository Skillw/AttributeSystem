package com.skillw.attsystem.internal.feature.compat.crackshot

import com.shampaggon.crackshot.events.WeaponShootEvent
import com.skillw.attsystem.api.AttrAPI.updateAttr
import taboolib.common.platform.Ghost
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync

internal object GunCompat {
    @SubscribeEvent
    @Ghost
    fun onShoot(event: WeaponShootEvent) {
        submitAsync {
            event.player.updateAttr()
        }
    }
}