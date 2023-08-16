package com.skillw.attsystem.internal.feature.realizer

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.attributeDataManager
import com.skillw.attsystem.AttributeSystem.attributeSystemAPI
import com.skillw.attsystem.api.event.HealthRegainEvent
import com.skillw.attsystem.internal.manager.ASConfig.fightSystem
import com.skillw.attsystem.util.Utils.validEntity
import com.skillw.fightsystem.api.FightAPI.isFighting
import com.skillw.pouvoir.api.feature.realizer.BaseRealizerManager
import com.skillw.pouvoir.api.feature.realizer.component.ScheduledRealizer
import com.skillw.pouvoir.api.feature.realizer.component.Switchable
import com.skillw.pouvoir.api.feature.realizer.component.Valuable
import com.skillw.pouvoir.api.feature.realizer.component.Vanillable
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityRegainHealthEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.util.unsafeLazy
import taboolib.common5.cbool
import kotlin.math.min

@AutoRegister
internal object HealthRegainRealizer : ScheduledRealizer("health-regain"), Switchable, Vanillable, Valuable {
    override val file by lazy {
        AttributeSystem.options.file!!
    }
    override val manager: BaseRealizerManager by unsafeLazy {
        AttributeSystem.realizerManager
    }
    override val defaultPeriod: Long = 10
    override val defaultEnable: Boolean
        get() = false
    override val defaultValue: String
        get() = "0"
    override val defaultVanilla: Boolean
        get() = true
    private val disableInFight: Boolean
        get() = config["disable-in-fighting"].cbool

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
    private fun LivingEntity.regain(amount: Double) {
        val maxHealth = maxHealth
        if (health >= maxHealth) return
        val event = HealthRegainEvent(this, amount).apply { call() }
        if (event.isCancelled) return
        val result = min(maxHealth, health + event.amount)
        health = result
    }

    override fun task() {
        for (uuid in attributeDataManager.keys) {
            val entity = uuid.validEntity()
            if (entity == null || !entity.isValid || entity.isDead) {
                attributeSystemAPI.remove(uuid)
                continue
            }
            if (disableInFight && fightSystem && entity.isFighting()) return
            entity.regain(value(entity))
        }
    }

    override fun whenEnable() {
        refreshTask()
    }

    override fun whenDisable() {
        cancelTask()
    }
}