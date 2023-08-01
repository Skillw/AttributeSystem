package com.skillw.attsystem.internal.feature.listener.fight

import com.skillw.attsystem.api.event.FightEvent
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.attsystem.util.StringUtils.material
import org.bukkit.entity.Player
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.nms.getName
import taboolib.platform.util.sendLang

private object DisableDamage {
    @SubscribeEvent(priority = EventPriority.MONITOR)
    fun disableMaterialAttack(event: FightEvent.Pre) {
        val attacker = event.fightData.attacker ?: return
        if (attacker !is Player || event.fightData["projectile"] == "true" || event.key != "attack-damage") return
        val material = attacker.inventory.itemInMainHand.type.name.material() ?: return
        if (attacker.hasPermission("as.damage_type.${material.name.lowercase()}")) return
        if (ASConfig.disableDamageTypes.contains(material)) {
            event.isCancelled = true
            attacker.sendLang("disable-damage-type", attacker.inventory.itemInMainHand.getName())
            return
        }
    }
}