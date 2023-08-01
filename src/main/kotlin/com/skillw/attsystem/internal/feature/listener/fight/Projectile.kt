package com.skillw.attsystem.internal.feature.listener.fight

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.fight.FightData
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.metadata.FixedMetadataValue
import taboolib.common.platform.Ghost
import taboolib.common.platform.event.SubscribeEvent

private object Projectile {
    @Ghost
    @SubscribeEvent
    fun projectileLaunch(event: ProjectileLaunchEvent) {
        val projectile = event.entity
        val shooter = (projectile.shooter as? LivingEntity?) ?: return
        val cacheData = FightData(shooter, null)
        projectile.setMetadata("ATTRIBUTE_SYSTEM_DATA", FixedMetadataValue(AttributeSystem.plugin, cacheData))
    }

    @Ghost
    @SubscribeEvent
    fun projectileHit(event: ProjectileHitEvent) {
        val projectile = event.entity
        val hitEntity = (event.hitEntity as? LivingEntity?) ?: return
        val velocity = projectile.velocity.subtract(hitEntity.velocity).length()
        projectile.setMetadata("ATTRIBUTE_SYSTEM_FORCE", FixedMetadataValue(AttributeSystem.plugin, velocity))
    }
}