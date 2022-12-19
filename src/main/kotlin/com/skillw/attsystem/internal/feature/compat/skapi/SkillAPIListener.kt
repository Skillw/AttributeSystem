package com.skillw.attsystem.internal.feature.compat.skapi

import com.skillw.attsystem.AttributeSystem
import com.skillw.pouvoir.util.EntityUtils.isAlive
import com.sucy.skill.api.event.SkillDamageEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.OptionalEvent
import taboolib.common.platform.event.SubscribeEvent

object SkillAPIListener {
    @SubscribeEvent(EventPriority.LOWEST, bind = "com.sucy.skill.api.event.SkillDamageEvent")
    fun e(optional: OptionalEvent) {
        val event = optional.get<SkillDamageEvent>()
        val attacker = event.damager
        val defender = event.target
        if (!attacker.isAlive() || !defender.isAlive()) {
            return
        }
        val originDamage = event.damage
        val triggerKey = "skill-api-${event.skill.key}-${event.classification}"
        if (!AttributeSystem.fightGroupManager.containsKey(triggerKey)) return
        val result = AttributeSystem.attributeSystemAPI.playerAttackCal(triggerKey, attacker, defender) {
            it["origin"] = originDamage
            it["event"] = event
        }
        event.damage = if (result == -1.0) originDamage else result
    }
}
