package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.event.MechanicRegisterEvent
import com.skillw.attsystem.api.fight.mechanic.Mechanic
import com.skillw.attsystem.api.manager.MechanicManager

object MechanicManagerImpl : MechanicManager() {
    override val key = "MechanicManager"
    override val priority: Int = 11
    override val subPouvoir = AttributeSystem

    override fun onEnable() {
        onReload()
    }

    override fun onReload() {
        this.entries.filter { it.value.release }.forEach { this.remove(it.key) }
    }

    override fun register(key: String, value: Mechanic) {
        val event = MechanicRegisterEvent(value)
        event.call()
        if (event.isCancelled) return
        put(key, value)
    }
}
