package com.skillw.attsystem.internal.feature.compat.mythicmobs.legacy

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.AttrAPI.read
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMechanicLoadEvent
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.Ghost
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit

private object MMIVListener {
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
        submit(delay = 5) {
            val attributes = event.mob.type.config.getStringList("Attributes")
            if (!attributes.isNullOrEmpty()) {
                if (attributes.isEmpty()) return@submit
                attributes.read(entity)?.let {
                    AttributeSystem.compiledAttrDataManager[entity.uniqueId].register(
                        "MYTHIC-BASE-ATTRIBUTE",
                        it
                    )
                }
                AttributeSystem.attributeSystemAPI.update(entity)
                entity.health = entity.maxHealth
            }
        }
    }

}