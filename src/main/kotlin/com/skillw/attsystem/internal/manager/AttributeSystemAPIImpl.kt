package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.AttrAPI.update
import com.skillw.attsystem.api.AttributeSystemAPI
import com.skillw.attsystem.api.event.FightEvent
import com.skillw.attsystem.api.fight.FightData
import com.skillw.attsystem.api.fight.message.MessageData
import com.skillw.attsystem.util.Utils.mirrorIfDebug
import com.skillw.pouvoir.util.EntityUtils.isAlive
import com.skillw.pouvoir.util.EntityUtils.livingEntity
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
import taboolib.common.util.sync
import taboolib.common5.Mirror
import java.util.*
import java.util.function.Consumer

object AttributeSystemAPIImpl : AttributeSystemAPI {

    override val key = "AttributeSystemAPI"
    override val priority: Int = 100
    override val subPouvoir = AttributeSystem
    private var realizeScheduled: PlatformExecutor.PlatformTask? = null

    private fun createRealizeScheduled(): PlatformExecutor.PlatformTask {
        return submitAsync(period = ASConfig.realizeSchedule) {
            val tasks = LinkedList<() -> Unit>()
            for (uuid in AttributeSystem.attributeDataManager.keys) {
                val entity = uuid.livingEntity()
                if (entity == null || !entity.isValid || entity.isDead) {
                    AttributeSystem.attributeSystemAPI.remove(uuid)
                    continue
                }

                entity.update()
                tasks += RealizeManagerImpl.newRealizeTask(entity)
            }
            sync {
                mirrorIfDebug("realize-attribute-all") {
                    tasks.forEach { it.invoke() }
                }
            }
        }
    }

    override fun onActive() {
        onReload()
    }

    override fun onReload() {
        realizeScheduled?.cancel()
        realizeScheduled = createRealizeScheduled()
        submitAsync(delay = 10) {
            for (uuid in AttributeSystem.attributeDataManager.keys) {
                val entity = uuid.livingEntity()
                if (entity == null || !entity.isValid || entity.isDead) {
                    remove(uuid)
                    continue
                }
                mirrorIfDebug("update-attribute") {
                    entity.update()
                }
            }
        }
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
        return mirrorIfDebug("fight-$key-cal") {
            val pre = FightEvent.Pre(key, fightData)
            pre.call()
            if (pre.isCancelled) return@mirrorIfDebug -0.1
            val eventFightData = pre.fightData
            AttributeSystem.fightGroupManager[key]!!.run(eventFightData)
            if (message)
                messageData.addAll(eventFightData.messageData)
            val post = FightEvent.Post(key, eventFightData)
            post.call()
            if (post.isCancelled) return@mirrorIfDebug -0.1
            if (message)
                submitAsync {
                    messageData.send(fightData.attacker as? Player?, fightData.defender as? Player?)
                }
            post.fightData.calResult()
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


    override fun update(entity: LivingEntity) {
        if (!entity.isAlive()) return
        mirrorIfDebug("update-entity") {
            mirrorIfDebug("update-equipment") {
                AttributeSystem.equipmentDataManager.update(entity)
            }
            mirrorIfDebug("update-attribute") {
                AttributeSystem.attributeDataManager.update(entity)
            }
        }
    }


    override fun remove(entity: LivingEntity) {
        this.remove(entity.uniqueId)
    }


    override fun remove(uuid: UUID) {
        AttributeSystem.attributeDataManager.remove(uuid)
        AttributeSystem.equipmentDataManager.remove(uuid)
        AttributeSystem.compiledAttrDataManager.remove(uuid)
    }

}
