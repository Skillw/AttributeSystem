package com.skillw.attsystem.internal.feature.compat.mythicmobs.common

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.event.AttributeUpdateEvent
import com.skillw.attsystem.internal.manager.ASConfig
import io.lumine.mythic.bukkit.MythicBukkit
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent
import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.Ghost
import taboolib.common.platform.event.SubscribeEvent

private object MMVListener {
    @Ghost
    @SubscribeEvent
    fun onMythicMechanicLoad(event: MythicMechanicLoadEvent) {
        when (event.mechanicName.lowercase()) {
            in listOf("att-damage", "attdamage") -> {
                event.register(AttributeDamageV(event.config))
            }

            in listOf("att-update", "attupdate") -> {
                event.register(DataUpdateV)
            }
        }
    }

    @Ghost
    @SubscribeEvent
    fun onMythicMobsSpawn(event: MythicMobSpawnEvent) {
        val entity = event.entity as? LivingEntity ?: return
        if (!event.mob.type.config.getStringList("Attributes").isNullOrEmpty())
            AttributeSystem.attributeSystemAPI.update(entity)
    }

    @SubscribeEvent
    fun onAttributeUpdateEvent(event: AttributeUpdateEvent.Pre) {
        if (!ASConfig.mythicMobsV) return
        val entity = event.entity
        if (entity !is LivingEntity) return
        MythicBukkit.inst().mobManager.getMythicMobInstance(entity)?.let { mob ->
            val attributes = mob.type.config.getStringList("Attributes")
            if (attributes.isEmpty()) return
            event.data.register(
                "MYTHIC-BASE-ATTRIBUTE",
                AttributeSystem.attributeSystemAPI.read(
                    attributes, entity
                )

            )
        }
    }
}