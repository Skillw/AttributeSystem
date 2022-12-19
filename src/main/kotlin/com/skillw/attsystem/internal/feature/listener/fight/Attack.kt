package com.skillw.attsystem.internal.feature.listener.fight

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.cooldownManager
import com.skillw.attsystem.AttributeSystem.formulaManager
import com.skillw.attsystem.api.AttrAPI.intoFighting
import com.skillw.attsystem.api.fight.FightData
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.attsystem.internal.manager.ASConfig.attackFightKeyMap
import com.skillw.attsystem.internal.manager.ASConfig.eveFightCal
import com.skillw.attsystem.internal.manager.ASConfig.forceBasedCooldown
import com.skillw.attsystem.internal.manager.ASConfig.isAttackAnyTime
import com.skillw.attsystem.internal.manager.ASConfig.isAttackForce
import com.skillw.attsystem.internal.manager.RealizeManagerImpl.ATTACK_DISTANCE
import com.skillw.attsystem.internal.manager.RealizeManagerImpl.ATTACK_SPEED
import com.skillw.attsystem.internal.manager.RealizeManagerImpl.getAttribute
import com.skillw.attsystem.util.BukkitAttribute
import com.skillw.pouvoir.util.EntityUtils.isAlive
import com.sucy.skill.api.skills.Skill
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.platform.util.attacker

internal object Attack {
    private val isSkillAPIDamage
        get() = ASConfig.skillAPI && Skill.isSkillDamage()


    @JvmStatic
    var skipCal = false
    private fun Entity.force() =
        if (hasMetadata("ATTRIBUTE_SYSTEM_FORCE")) getMetadata("ATTRIBUTE_SYSTEM_FORCE")[0].asDouble() else -1.0


    @SubscribeEvent(priority = EventPriority.LOW)
    fun attack(event: EntityDamageByEntityEvent) {
        val attacker = event.attacker ?: return
        val defender = event.entity
        if (!attacker.isAlive() || !defender.isAlive() || defender is ArmorStand) {
            return
        }
        defender as LivingEntity
        if (attacker !is Player && defender !is Player && !eveFightCal) return
        if (skipCal) {
            skipCal = false
            return
        }
        if (event.isCancelled) return
        if (isSkillAPIDamage) return
        if (!ASConfig.isVanillaArmor) {
            event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0.0)
        }
        val originDamage = event.finalDamage
        val isAttack = event.cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK
        val isProjectile = event.cause == EntityDamageEvent.DamageCause.PROJECTILE
        if (!isAttack && !isProjectile) return
        var force = 1.0
        if (attacker is Player) {
            val main = attacker.inventory.itemInMainHand
            //CrashShot
            if (isProjectile && event.damager.hasMetadata("projParentNode") && ASConfig.skipCrashShot) return

            val isCooldown = cooldownManager.isItemCoolDown(
                attacker,
                main
            )
            //攻击距离
            if (isAttack && defender.location.distance(attacker.location) > formulaManager[attacker, ATTACK_DISTANCE]) {
                event.isCancelled = true
                return
            }

            //在冷却 && 不能随时攻击 就取消
            if (!isAttackAnyTime && isCooldown) {
                event.isCancelled = true
                return
            }

            force = event.damager.force().let {
                if (it != -1.0) it
                else when {
                    isAttackAnyTime -> when {
                        isAttackForce -> if (forceBasedCooldown) {
                            cooldownManager.pull(attacker, main.type)
                        } else {
                            attacker.getAttribute(BukkitAttribute.ATTACK_DAMAGE)?.value?.let { attackDamage ->
                                event.getOriginalDamage(EntityDamageEvent.DamageModifier.BASE) / attackDamage
                            } ?: return@let 1.0
                        }

                        else -> 1.0
                    }

                    !isAttackAnyTime && isAttackForce -> 1.0

                    else -> 1.0
                }
            }
            if (isAttackForce && force < ASConfig.minForce) {
                event.isCancelled = true
                return
            }
        }
        val fightKey =
            if (attacker.isOp) "attack-damage" else attackFightKeyMap.filterKeys { attacker.hasPermission(it) }.values.firstOrNull()
                ?: "attack-damage"
        val result = AttributeSystem.attributeSystemAPI.runFight(fightKey, FightData(attacker, defender) {
            it["origin"] = originDamage
            it["force"] = force
            it["event"] = event
            it["projectile"] = isProjectile.toString()
        })
        if (result <= 0.0) {
            event.isCancelled = true
            if (attacker !is Player || isProjectile) return
            cooldownManager.setItemCoolDown(
                attacker,
                attacker.inventory.itemInMainHand,
                formulaManager[attacker.uniqueId, ATTACK_SPEED]
            )
            return
        }
        event.damage = result
        attacker.intoFighting()
        defender.intoFighting()
        if (attacker !is Player || isProjectile) return
        cooldownManager.setItemCoolDown(
            attacker,
            attacker.inventory.itemInMainHand,
            formulaManager[attacker.uniqueId, ATTACK_SPEED]
        )
        submit {
            defender.noDamageTicks = ASConfig.noDamageTicks
        }
    }

}