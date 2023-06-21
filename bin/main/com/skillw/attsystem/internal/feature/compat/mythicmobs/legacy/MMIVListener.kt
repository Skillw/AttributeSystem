package com.skillw.attsystem.internal.feature.compat.mythicmobs.legacy

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.event.AttributeUpdateEvent
import com.skillw.attsystem.internal.manager.ASConfig
import io.lumine.xikage.mythicmobs.MythicMobs
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMechanicLoadEvent
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.Ghost
import taboolib.common.platform.event.SubscribeEvent

internal object MMIVListener {
    @Ghost
    @SubscribeEvent
    fun onMythicMechanicLoad(event: MythicMechanicLoadEvent) {
        when (event.mechanicName.lowercase()) {
            in listOf("att-damage", "attdamage") -> {
                event.register(AttributeDamageIV(event.config.line, event.config))
            }

            in listOf("att-update", "attupdate") -> {
                event.register(DataUpdateIV(event.config.line, event.config))
            }
        }
    }

    @Ghost
    @SubscribeEvent
    fun onMythicMobsSpawn(event: MythicMobSpawnEvent) {
        val entity = event.entity as? LivingEntity ?: return
        AttributeSystem.attributeSystemAPI.update(entity)
    }

    @SubscribeEvent
    fun onAttributeUpdateEvent(event: AttributeUpdateEvent.Pre) {
        if (!ASConfig.mythicMobsIV) return
        val entity = event.entity
        if (entity !is LivingEntity) return
        MythicMobs.inst().mobManager.getMythicMobInstance(entity)?.let { mob ->
            val config = mob.type.config
            if (!config.isList("Attributes")) return
            val attributes = config.getStringList("Attributes")
            if (attributes.isEmpty()) return
            event.data.register(
                "MYTHIC-BASE-ATTRIBUTE",
                AttributeSystem.attributeSystemAPI.read(attributes, entity)
            )
        }
    }
}