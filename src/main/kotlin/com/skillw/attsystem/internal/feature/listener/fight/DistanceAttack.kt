package com.skillw.attsystem.internal.feature.listener.fight

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.attsystem.internal.manager.ASConfig.creativeDistance
import com.skillw.attsystem.internal.manager.ASConfig.defaultDistance
import com.skillw.attsystem.internal.manager.RealizeManagerImpl.ATTACK_DISTANCE
import com.skillw.attsystem.internal.manager.RealizeManagerImpl.getAttribute
import com.skillw.attsystem.util.AntiCheatUtils.bypassAntiCheat
import com.skillw.attsystem.util.AntiCheatUtils.recoverAntiCheat
import com.skillw.attsystem.util.BukkitAttribute
import com.skillw.pouvoir.Pouvoir
import com.skillw.pouvoir.util.EntityUtils.getEntityRayHit
import org.bukkit.GameMode
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import taboolib.common.platform.ProxyParticle
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.isPrimaryThread
import taboolib.common.platform.function.submitAsync
import taboolib.common.util.Location
import taboolib.common.util.sync
import taboolib.library.xseries.XSound

private object DistanceAttack {
    fun distanceDamage(player: Player, entity: LivingEntity) {
        bypassAntiCheat(player)
        val attackDamage = player.getAttribute(BukkitAttribute.ATTACK_DAMAGE)?.value ?: 0.0
        val force = when {
            //如果无视攻击速度，可以随时攻击，并开启近战蓄力
            ASConfig.isAttackAnyTime && ASConfig.isAttackForce -> {
                AttributeSystem.cooldownManager.pull(player, player.inventory.itemInMainHand.type)
            }
            //如果无视攻击速度，可以随时攻击，并关闭近战蓄力
            !ASConfig.isAttackAnyTime && ASConfig.isAttackForce -> 1.0

            else -> 1.0
        }
        if (ASConfig.isDistanceSound) XSound.ENTITY_PLAYER_ATTACK_SWEEP.play(player, 1.0f, 1.0f)
        if (ASConfig.isDistanceEffect) {
            val location = entity.eyeLocation
            ProxyParticle.SWEEP_ATTACK.sendTo(
                adaptPlayer(player),
                Location(player.world.name, location.x, location.y, location.z)
            )
        }
        entity.damage(attackDamage.coerceAtLeast(1.0) * force, player)
        recoverAntiCheat(player)
    }

    fun handleEvent(event: PlayerInteractEvent) {
        val player = event.player
        val uuid = player.uniqueId
        if (event.action != Action.LEFT_CLICK_AIR) {
            return
        }
        val gameMode = player.gameMode
        if (gameMode == GameMode.SPECTATOR) {
            return
        }
        val attackDistance = AttributeSystem.formulaManager[uuid, ATTACK_DISTANCE]
        val entity = player.getEntityRayHit(attackDistance)
        if (entity == null || entity.location.distance(player.location) <= if (gameMode == GameMode.CREATIVE) creativeDistance else defaultDistance
        ) {
            return
        }
        if (isPrimaryThread) distanceDamage(player, entity) else sync { distanceDamage(player, entity) }
    }

    @SubscribeEvent
    fun distanceAttack(event: PlayerInteractEvent) {
        if (Pouvoir.sync)
            handleEvent(event)
        else submitAsync { handleEvent(event) }
    }
}