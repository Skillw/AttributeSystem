package com.skillw.attsystem.internal.feature.compat.mythicmobs.legacy

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.event.AttributeUpdateEvent
import com.skillw.attsystem.internal.manager.ASConfig
import io.lumine.xikage.mythicmobs.MythicMobs
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMechanicLoadEvent
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.event.OptionalEvent
import taboolib.common.platform.event.SubscribeEvent

internal object MMIVListener {
    @SubscribeEvent(bind = "io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMechanicLoadEvent")
    fun onMythicMechanicLoad(optionalEvent: OptionalEvent) {
        val event = optionalEvent.get<MythicMechanicLoadEvent>()
        when (event.mechanicName.lowercase()) {
            in listOf("att-damage", "attdamage") -> {
                event.register(AttributeDamageIV(event.config.line, event.config))
            }

            in listOf("att-update", "attupdate") -> {
                event.register(DataUpdateIV(event.config.line, event.config))
            }
        }
    }

    @SubscribeEvent(bind = "io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent")
    fun onMythicMobsSpawn(optionalEvent: OptionalEvent) {
        val event = optionalEvent.get<MythicMobSpawnEvent>()
        val uuid = event.entity.uniqueId
        val entity = event.entity as? LivingEntity ?: return
        AttributeSystem.attributeSystemAPI.update(entity)
    }

    @SubscribeEvent(bind = "io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent")
    fun onAttributeUpdateEvent(event: AttributeUpdateEvent.Post) {
        if (!ASConfig.mythicMobsIV) return
        val uuid = event.entity.uniqueId
        MythicMobs.inst().mobManager.getActiveMob(uuid).run {
            if (isPresent) {
                val mob = this.get()
                if (mob.type.config.getStringList("Attributes").isNullOrEmpty()) return
                val entity = mob.entity as? LivingEntity ?: return
                event.compound.register(
                    "MYTHIC-BASE-ATTRIBUTE",
                    AttributeSystem.attributeSystemAPI.read(mob.type.config.getStringList("Attributes"), entity)
                )
            }
        }
    }
}