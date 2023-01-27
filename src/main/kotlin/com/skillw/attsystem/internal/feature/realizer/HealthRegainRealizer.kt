package com.skillw.attsystem.internal.feature.realizer

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.attributeDataManager
import com.skillw.attsystem.api.event.HealthRegainEvent
import com.skillw.attsystem.api.realizer.*
import com.skillw.attsystem.api.realizer.component.*
import com.skillw.attsystem.api.realizer.component.sub.ScheduledRealizer
import com.skillw.attsystem.api.realizer.component.sub.Switchable
import com.skillw.attsystem.api.realizer.component.sub.Valuable
import com.skillw.attsystem.api.realizer.component.sub.Vanillable
import com.skillw.attsystem.internal.manager.ASConfig.fightSystem
import com.skillw.fightsystem.api.FightAPI.isFighting
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister
import org.bukkit.Bukkit
import org.bukkit.entity.Damageable
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityRegainHealthEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common5.cbool
import kotlin.math.min

@AutoRegister
internal object HealthRegainRealizer : ScheduledRealizer("health-regain"), Switchable, Vanillable, Valuable {
    override val file by lazy {
        AttributeSystem.options.file!!
    }

    override val defaultPeriod: Long = 10
    override val defaultEnable: Boolean
        get() = false
    override val defaultValue: String
        get() = "0"
    override val defaultVanilla: Boolean
        get() = true
    private val disableInFight: Boolean
        get() = defaultConfig["disable-in-fighting"].cbool

    init {
        defaultConfig["disable-in-fighting"] = false
    }

    @SubscribeEvent
    fun onVanillaRegain(event: EntityRegainHealthEvent) {
        if (!isEnableVanilla()) {
            event.isCancelled = true
            return
        }
        val regainEvent = HealthRegainEvent(event.entity, event.amount)
        regainEvent.call()
        event.amount = regainEvent.amount
    }

    @Suppress("DEPRECATION")
    private fun Damageable.regain(amount: Double) {
        val maxHealth = maxHealth
        if (health == maxHealth) return
        val event = HealthRegainEvent(this, amount).apply { call() }
        if (event.isCancelled) return
        health = min(maxHealth, health + event.amount)
    }

    override fun task() {
        Bukkit.getWorlds().forEach { world ->
            world.entities.filterIsInstance<LivingEntity>().forEach entities@{ entity ->
                if (!attributeDataManager.containsKey(entity.uniqueId)) return
                if (disableInFight && fightSystem && entity.isFighting()) return
                entity.regain(value(entity))
            }
        }
    }

    override fun whenEnable() {
        refreshTask()
    }

    override fun whenDisable() {
        cancelTask()
    }
}