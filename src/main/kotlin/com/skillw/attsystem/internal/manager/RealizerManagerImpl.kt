package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.api.manager.RealizerManager
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

object RealizerManagerImpl : RealizerManager() {
    override val priority: Int = 999

    @Awake(LifeCycle.DISABLE)
    fun disable() {
        onDisable()
    }
}