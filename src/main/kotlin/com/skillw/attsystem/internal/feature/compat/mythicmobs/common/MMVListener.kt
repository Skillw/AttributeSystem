package com.skillw.attsystem.internal.feature.compat.mythicmobs.common

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.AttrAPI.read
import com.skillw.attsystem.api.AttrAPI.updateSync
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent
import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.Ghost
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit

internal object MMVListener {
    @Ghost
    @SubscribeEvent
    fun onMythicMechanicLoad(event: MythicMechanicLoadEvent) {
        when (event.mechanicName.lowercase()) {
            in listOf("att-update", "attupdate") -> {
                event.register(DataUpdateV)
            }
        }
    }

    @Ghost
    @SubscribeEvent
    fun onMythicMobsSpawn(event: MythicMobSpawnEvent) {
        val entity = event.entity as? LivingEntity ?: return
        val attributes = event.mob.type.config.getStringList("Attributes")
        if (!attributes.isNullOrEmpty())
            submit(delay = 5) {
                attributes.read(entity)?.let {
                    AttributeSystem.compiledAttrDataManager[entity.uniqueId]?.register(
                        "MYTHIC-BASE-ATTRIBUTE",
                        it
                    )
                }
                entity.updateSync()
                entity.health = entity.maxHealth
            }
    }

}