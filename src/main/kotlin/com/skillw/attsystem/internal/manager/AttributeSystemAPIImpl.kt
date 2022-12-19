package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.AttributeSystemAPI
import com.skillw.attsystem.api.attribute.compound.AttributeData
import com.skillw.attsystem.api.event.FightEvent
import com.skillw.attsystem.api.event.StringsReadEvent
import com.skillw.attsystem.api.fight.FightData
import com.skillw.attsystem.api.fight.message.MessageData
import com.skillw.attsystem.internal.core.read.ReadGroup
import com.skillw.attsystem.internal.feature.listener.fight.Attack.skipCal
import com.skillw.attsystem.internal.manager.ASConfig.ignores
import com.skillw.pouvoir.util.EntityUtils.isAlive
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Zombie
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.submitAsync
import taboolib.common.platform.service.PlatformExecutor
import taboolib.common5.Mirror
import taboolib.common5.mirrorNow
import taboolib.module.chat.uncolored
import java.util.*
import java.util.function.Consumer

object AttributeSystemAPIImpl : AttributeSystemAPI {

    override val key = "AttributeSystemAPI"
    override val priority: Int = 100
    override val subPouvoir = AttributeSystem
    private var task: PlatformExecutor.PlatformTask? = null

    override fun onActive() {
        onReload()
    }

    @Awake(LifeCycle.ACTIVE)
    fun initSystem() {
        submit {
            val world = Bukkit.getWorlds().first()
            val entityA = world.spawnEntity(Location(world, 0.0, 255.0, 0.0), EntityType.ZOMBIE) as Zombie
            val entityB = world.spawnEntity(Location(world, 0.0, 255.0, 0.0), EntityType.ZOMBIE) as Zombie
            entityA.setGravity(false)
            entityB.setGravity(false)
            entityA.damage(1.0, entityB)
            entityA.remove()
            entityB.remove()
        }
        Mirror.mirrorData.clear()
    }

    override fun skipNextDamageCal() {
        skipCal = true
    }

    @Deprecated("请使用 [runFight] 方法")
    override fun entityAttackCal(
        key: String,
        attacker: LivingEntity?,
        defender: LivingEntity,
        consumer: Consumer<FightData>,
    ): Double {
        return runFight(key, FightData(attacker, defender).also { consumer.accept(it) }, false)
    }

    override fun runFight(key: String, data: FightData, message: Boolean): Double {
        if (!AttributeSystem.fightGroupManager.containsKey(key)) return -1.0
        val fightData = data.apply {
            this.putIfAbsent("projectile", false)
            this["type"] = when {
                attacker is Player && defender is Player -> "PVP"
                attacker is Player && defender !is Player -> "PVE"
                attacker !is Player && defender !is Player -> "EVE"
                else -> "EVE"
            }
            calMessage = message
        }
        val messageData = MessageData()
        return mirrorNow<Double>("fight-$key-cal") {
            val pre = FightEvent.Post(key, fightData)
            pre.call()
            if (pre.isCancelled) return@mirrorNow -0.1
            val eventFightData = pre.fightData
            AttributeSystem.fightGroupManager[key]!!.run(eventFightData)
            if (message)
                messageData.addAll(eventFightData.messageData)
            val after = FightEvent.After(key, eventFightData)
            after.call()
            if (after.isCancelled) return@mirrorNow -0.1
            if (message)
                submitAsync {
                    messageData.send(fightData.attacker as? Player?, fightData.defender as? Player?)
                }
            after.fightData.calResult()
        }
    }

    @Deprecated("请使用 [runFight] 方法")
    @Suppress("DuplicatedCode")
    override fun playerAttackCal(
        key: String,
        attacker: LivingEntity?,
        defender: LivingEntity,
        consumer: Consumer<FightData>,
    ): Double {
        return runFight(key, FightData(attacker, defender).also { consumer.accept(it) }, true)
    }

    override fun read(
        strings: Collection<String>,
        entity: LivingEntity?,
        slot: String?,
    ): AttributeData {
        return mirrorNow("read-strings") {
            val attributeData = AttributeData()
            if (!AttributeSystem.conditionManager.conditionStrings(
                    slot,
                    entity,
                    strings
                )
            ) {
                return@mirrorNow attributeData
            }
            strings@ for (string in strings) {
                if (ignores.any { string.uncolored().contains(it) }) continue
                val matcher = ASConfig.lineConditionPattern.matcher(string)
                if (matcher.find()) {
                    try {
                        val requirements = matcher.group("requirement")
                        if (!AttributeSystem.conditionManager.lineConditions(slot, requirements, entity)) continue
                    } catch (_: IllegalStateException) {
                    } catch (_: IllegalArgumentException) {
                    }
                }
                att@ for (attribute in AttributeSystem.attributeManager.attributes) {
                    val read = attribute.readPattern
                    if (read !is ReadGroup<*>) continue
                    val status = read.read(string, attribute, entity, slot)
                    if (status != null) {
                        attributeData.operation(attribute, status)
                        continue@strings
                    }
                }
            }
            val event = StringsReadEvent(entity, strings, attributeData)
            event.call()
            if (!event.isCancelled) event.attrData else AttributeData()
        }
    }

    override fun update(entity: LivingEntity) {
        if (!entity.isAlive()) return
        AttributeSystem.equipmentDataManager.update(entity)
        AttributeSystem.attributeDataManager.update(entity)
        AttributeSystem.realizeManager.realize(entity)
    }


    override fun remove(entity: LivingEntity) {
        this.remove(entity.uniqueId)
    }


    override fun remove(uuid: UUID) {
        AttributeSystem.attributeDataManager.remove(uuid)
        AttributeSystem.equipmentDataManager.remove(uuid)

//        AttributeSystem.getShieldDataManager().removeByKey(uuid)
    }

}
