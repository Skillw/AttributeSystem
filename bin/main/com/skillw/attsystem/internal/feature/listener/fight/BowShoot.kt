package com.skillw.attsystem.internal.feature.listener.fight

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.fight.FightData
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.metadata.FixedMetadataValue
import taboolib.common.platform.event.SubscribeEvent

private object BowShoot {
    @SubscribeEvent
    fun forceAddition(event: EntityShootBowEvent) {
        val attacker = event.entity
        val cacheData = FightData(attacker, null)
        event.projectile.setMetadata("ATTRIBUTE_SYSTEM_DATA", FixedMetadataValue(AttributeSystem.plugin, cacheData))
        event.projectile.setMetadata("ATTRIBUTE_SYSTEM_FORCE", FixedMetadataValue(AttributeSystem.plugin, event.force))
    }
}