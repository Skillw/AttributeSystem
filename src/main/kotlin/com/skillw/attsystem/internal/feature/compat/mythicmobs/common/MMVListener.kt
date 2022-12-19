package com.skillw.attsystem.internal.feature.compat.mythicmobs.common

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.event.AttributeUpdateEvent
import com.skillw.attsystem.internal.manager.ASConfig
import io.lumine.mythic.bukkit.MythicBukkit
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent
import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.event.OptionalEvent
import taboolib.common.platform.event.SubscribeEvent

private object MMVListener {
    @SubscribeEvent(bind = "io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent")
    fun onMythicMechanicLoad(optionalEvent: OptionalEvent) {
        val event = optionalEvent.get<MythicMechanicLoadEvent>()
        when (event.mechanicName.lowercase()) {
            in listOf("att-damage", "attdamage") -> {
                event.register(AttributeDamageV(event.config))
            }

            in listOf("att-update", "attupdate") -> {
                event.register(DataUpdateV)
            }
        }
    }

    @SubscribeEvent(bind = "io.lumine.mythic.bukkit.events.MythicMobSpawnEvent")
    fun onMythicMobsSpawn(optionalEvent: OptionalEvent) {
        val event = optionalEvent.get<MythicMobSpawnEvent>()
        val entity = event.entity as? LivingEntity ?: return
        if (!event.mob.type.config.getStringList("Attributes").isNullOrEmpty())
            AttributeSystem.attributeSystemAPI.update(entity)
    }

    @SubscribeEvent
    fun onAttributeUpdateEvent(event: AttributeUpdateEvent.Post) {
        if (!ASConfig.mythicMobsV) return
        val uuid = event.entity.uniqueId
        MythicBukkit.inst().mobManager.getActiveMob(uuid).run {
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