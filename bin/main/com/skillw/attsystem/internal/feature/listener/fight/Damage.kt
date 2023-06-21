package com.skillw.attsystem.internal.feature.listener.fight

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.AttrAPI.intoFighting
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.pouvoir.util.EntityUtils.isAlive
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.attacker

private object Damage {
    @SubscribeEvent(priority = EventPriority.MONITOR)
    fun damageEntity(event: EntityDamageEvent) {
        val attacker: LivingEntity? = if (event is EntityDamageByEntityEvent) event.attacker else null
        val defender = event.entity as? LivingEntity? ?: return
        if (!defender.isAlive()) return
        val cause = event.cause.name.lowercase()
        val key = "damage-cause-$cause"
        if (!AttributeSystem.fightGroupManager.containsKey(key)) return
        val result = AttributeSystem.attributeSystemAPI.playerAttackCal(key, attacker, defender) {
            it["origin"] = event.damage; it["event"] = event
        }
        if (result > 0.0) {
            if (!ASConfig.isVanillaArmor && attacker?.type != EntityType.ARMOR_STAND) {
                event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0.0)
            }
            event.setDamage(EntityDamageEvent.DamageModifier.BASE, result)
            attacker?.intoFighting()
            defender.intoFighting()
        } else if (result < 0.0) {
            event.isCancelled = true
        }
    }
}