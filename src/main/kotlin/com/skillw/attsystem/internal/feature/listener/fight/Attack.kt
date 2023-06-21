package com.skillw.attsystem.internal.feature.listener.fight

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.cooldownManager
import com.skillw.attsystem.AttributeSystem.formulaManager
import com.skillw.attsystem.api.AttrAPI.intoFighting
import com.skillw.attsystem.api.fight.FightData
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.attsystem.internal.manager.ASConfig.arrowCache
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
    var nextAttackCal = false
    private fun Entity.force(): Double? =
        if (hasMetadata("ATTRIBUTE_SYSTEM_FORCE")) getMetadata("ATTRIBUTE_SYSTEM_FORCE")[0].asDouble() else null

    private fun Entity.cacheData(): FightData? =
        if (hasMetadata("ATTRIBUTE_SYSTEM_DATA")) getMetadata("ATTRIBUTE_SYSTEM_DATA")[0].value() as? FightData else null

    @SubscribeEvent(priority = EventPriority.LOW)
    fun attack(event: EntityDamageByEntityEvent) {
        //如果攻击原因不是 ENTITY_ATTACK 和 PROJECTILE 则跳过计算
        val isAttack = event.cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK
        val isProjectile = event.cause == EntityDamageEvent.DamageCause.PROJECTILE
        if (!isAttack && !isProjectile) return

        val attacker = event.attacker ?: return
        val defender = event.entity
        //判断是否都是存活实体                                防御方为盔甲架则跳过计算
        if (!attacker.isAlive() || !defender.isAlive() || defender is ArmorStand) return
        defender as LivingEntity

        //是否是EVE (非玩家 打 非玩家)                       如果关闭EVE计算则跳过计算
        if (attacker !is Player && defender !is Player && !eveFightCal) return

        //是否跳过这次计算
        if (nextAttackCal) {
            nextAttackCal = false
            return
        }

        //事件取消则跳过计算
        if (event.isCancelled) return

        //是 SkillAPI 的伤害则跳过计算
        if (isSkillAPIDamage) return

        //处理原版护甲
        if (!ASConfig.isVanillaArmor) {
            event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0.0)
        }
        //如果不是原版弓/弩攻击 则跳过计算
        if (isProjectile && event.damager.force() == null) return
        //原伤害
        val originDamage = event.finalDamage

        //蓄力程度
        var force = 1.0
        if (attacker is Player) {
            val main = attacker.inventory.itemInMainHand
            val isCooldown = cooldownManager.isItemCoolDown(attacker, main)
            //不满足攻击距离，则取消伤害并跳过计算
            if (isAttack && defender.location.distance(attacker.location) > formulaManager[attacker, ATTACK_DISTANCE]) {
                event.isCancelled = true
                return
            }

            //在冷却 && 不能随时攻击 就取消
            if (!isAttackAnyTime && isCooldown) {
                event.isCancelled = true
                return
            }

            //计算蓄力程度
            //  这个函数是获取弓/弩的蓄力程度，若返回null则代表不是抛射物攻击，进而进行近战时的蓄力计算
            force = event.damager.force() ?: when {
                //如果无视攻击速度，可以随时攻击，并开启近战蓄力
                isAttackAnyTime && isAttackForce -> when {
                    //基于AS的冷却系统计算蓄力
                    forceBasedCooldown -> {
                        cooldownManager.pull(attacker, main.type).also {
                            //Fix 距离攻击连点满蓄力BUG:
                            //pull完cooldown，还没执行到冷却玩家攻击的代码段，玩家就再次攻击了
                            //至于为什么 距离攻击 中的处理会执行的这么慢:
                            //  距离攻击中使用了 Entity#damage 函数让玩家对实体造成伤害
                            //  最慢需要n个tick (n*50ms)后，这个EventHandler才会处理，造成玩家冷却延迟
                            //因此在这里在pull时就冷却一下
                            cooldownManager.setItemCoolDown(
                                attacker,
                                attacker.inventory.itemInMainHand,
                                formulaManager[attacker.uniqueId, ATTACK_SPEED]
                            )
                        }
                    }

                    //基于原版伤害计算蓄力
                    else -> {
                        (attacker.getAttribute(BukkitAttribute.ATTACK_DAMAGE)?.value?.let { attackDamage ->
                            event.getOriginalDamage(EntityDamageEvent.DamageModifier.BASE) / attackDamage
                        } ?: 1.0)
                    }
                }
                //如果无视攻击速度，可以随时攻击，并关闭近战蓄力
                !isAttackAnyTime && isAttackForce -> 1.0

                else -> 1.0
            }
            //如果小于阈值。则跳过计算（防连点器
            if (isAttackForce && force < ASConfig.minForce) {
                event.isCancelled = true
                return
            }
        }
        //处理战斗组id
        val fightKey =
            when {
                //是op的话就直接"attack-damage"，不参与权限计算
                attacker.isOp -> "attack-damage"
                else -> attackFightKeyMap.filterKeys { attacker.hasPermission(it) }.values.firstOrNull()
                    ?: "attack-damage"
            }
        val cacheData = event.damager.cacheData()
        val data = if (arrowCache && cacheData != null) cacheData.also { it.defender = defender } else FightData(
            attacker,
            defender
        )
        //运行战斗组并返回结果
        val result = AttributeSystem.attributeSystemAPI.runFight(fightKey, data.also {
            //往里塞参数
            it["origin"] = originDamage
            it["force"] = force
            it["event"] = event
            it["projectile"] = isProjectile.toString()
        })
        //结果小于等于零，代表MISS 未命中
        if (result <= 0.0) {
            event.isCancelled = true
            if (attacker !is Player || isProjectile) return
            //还是要让玩家的武器冷却的
            cooldownManager.setItemCoolDown(
                attacker,
                attacker.inventory.itemInMainHand,
                formulaManager[attacker.uniqueId, ATTACK_SPEED]
            )
            return
        }
        //设置伤害
        event.damage = result
        //让双方都进入战斗状态
        attacker.intoFighting()
        defender.intoFighting()
        if (attacker !is Player || isProjectile) return
        //让玩家的武器冷却的
        cooldownManager.setItemCoolDown(
            attacker,
            attacker.inventory.itemInMainHand,
            formulaManager[attacker.uniqueId, ATTACK_SPEED]
        )
        //无伤帧
        submit {
            defender.noDamageTicks = ASConfig.noDamageTicks
        }
    }

}