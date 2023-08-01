package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.fightStatusManager
import com.skillw.attsystem.AttributeSystem.formulaManager
import com.skillw.attsystem.api.event.HealthRegainEvent
import com.skillw.attsystem.api.fight.FightData
import com.skillw.attsystem.api.manager.RealizeManager
import com.skillw.attsystem.internal.feature.message.ASHologramGroup
import com.skillw.attsystem.internal.manager.ASConfig.defaultMaxHealth
import com.skillw.attsystem.internal.manager.ASConfig.disableRegainOnFight
import com.skillw.attsystem.internal.manager.ASConfig.isVanillaAttackSpeed
import com.skillw.attsystem.internal.manager.ASConfig.isVanillaMaxHealth
import com.skillw.attsystem.internal.manager.ASConfig.isVanillaMovementSpeed
import com.skillw.attsystem.internal.manager.ASConfig.skillAPI
import com.skillw.attsystem.util.AntiCheatUtils.bypassAntiCheat
import com.skillw.attsystem.util.AntiCheatUtils.recoverAntiCheat
import com.skillw.attsystem.util.BukkitAttribute
import com.skillw.pouvoir.api.PouvoirAPI.placeholder
import com.skillw.pouvoir.api.map.BaseMap
import com.skillw.pouvoir.util.EntityUtils.isAlive
import com.skillw.pouvoir.util.EntityUtils.livingEntity
import com.skillw.pouvoir.util.GsonUtils.encodeJson
import com.skillw.pouvoir.util.MapUtils.put
import com.skillw.pouvoir.util.NumberUtils.format
import com.sucy.skill.SkillAPI
import com.sucy.skill.api.player.PlayerClass
import org.bukkit.attribute.AttributeInstance
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.platform.function.isPrimaryThread
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.submitAsync
import taboolib.common.platform.service.PlatformExecutor
import taboolib.common.util.sync
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.invokeMethod
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsClass
import taboolib.module.nms.obcClass
import java.util.*

object RealizeManagerImpl : RealizeManager() {
    override val key = "RealizeManager"
    override val priority: Int = 9
    override val subPouvoir = AttributeSystem
    override var healthRegainScheduled: PlatformExecutor.PlatformTask? = null

    const val MAX_HEALTH = "max-health"
    const val MOVEMENT_SPEED = "movement-speed"
    const val HEALTH_REGAIN = "health-regain"
    const val KNOCKBACK_RESISTANCE = "knockback-resistance"

    const val ATTACK_SPEED = "attack-speed"
    const val ATTACK_DISTANCE = "attack-distance"
    const val LUCK = "luck"

    private fun createHealthRegainScheduled(): PlatformExecutor.PlatformTask {
        fun calRegain(entity: LivingEntity): Double {
            val uuid = entity.uniqueId
            val maxHealth = entity.getAttribute(BukkitAttribute.MAX_HEALTH)?.value ?: return -1.0
            if (entity.health == maxHealth) return -1.0
            var healthRegain = formulaManager[uuid, HEALTH_REGAIN]
            if (healthRegain == -1.0) return -1.0
            val event = HealthRegainEvent(entity, healthRegain)
            event.call()
            if (event.isCancelled) return -1.0
            val health = entity.health
            val value = health + event.regain
            if (value >= maxHealth) {
                entity.health = maxHealth
                healthRegain = maxHealth - health
            } else {
                entity.health = value
            }
            return healthRegain
        }
        return submit(period = ASConfig.healthRegainSchedule) {
            for (uuid in AttributeSystem.attributeDataManager.keys) {
                val entity = uuid.livingEntity()
                if (entity == null) {
                    AttributeSystem.attributeSystemAPI.remove(uuid)
                    continue
                }
                if (fightStatusManager.isFighting(entity) && disableRegainOnFight) continue
                val healthRegain = calRegain(entity)
                if (healthRegain <= 0) continue
                if (entity is Player && AttributeSystem.personalManager.getPreference(uuid).regainHolo) submitAsync {
                    val section =
                        ASConfig["message"].getConfigurationSection("health-regain-holo") ?: return@submitAsync
                    val text =
                        (section.getString("text") ?: "&2+ 7a{value}").replace(
                            "{value}",
                            healthRegain.format()
                        )
                    val distance = (section.getString("distance") ?: "8").toDoubleOrNull() ?: 8.0
                    val holo = ASHologramGroup(
                        mutableListOf(text.placeholder(entity)),
                        entity.eyeLocation,
                        "health-regain-holo",
                        FightData(entity, null)
                    )
                    val players = entity.getNearbyEntities(distance, distance, distance).filterIsInstance<Player>()
                        .toMutableList()
                    players.add(entity)
                    holo.sendTo(*players.toTypedArray())
                }
            }
        }
    }

    private fun getSkillAPIHealth(player: Player): Int {
        return if (skillAPI) SkillAPI.getPlayerData(player).classes.stream()
            .mapToInt { aClass: PlayerClass -> aClass.health.toInt() }.sum() else 0
    }

    private val attMap = BaseMap<UUID, BaseMap<BukkitAttribute, AttributeModifier>>()


    private fun realizeAttribute(
        entity: LivingEntity,
        bukkitAttribute: BukkitAttribute,
        value: Double,
        vanilla: Boolean = false,
    ) {
        val uuid = entity.uniqueId
        val attribute = entity.getAttribute(bukkitAttribute) ?: return
        with(attribute) {
            if (attMap.containsKey(uuid) && attMap[uuid]!!.containsKey(bukkitAttribute)) {
                removeModifier(attMap[uuid]!![bukkitAttribute]!!)
            }
            if (!vanilla) {
                for (modifier in modifiers) {
                    removeModifier(modifier)
                }
            }
            if (value == -1.0) return
            val result = if (vanilla) value else value - baseValue
            val attributeModifier = AttributeModifier(
                uuid,
                bukkitAttribute.minecraftKey,
                result,
                AttributeModifier.Operation.ADD_NUMBER
            )
            if (result != 0.0)
                attMap.put(uuid, bukkitAttribute, attributeModifier)
            removeModifier(attributeModifier)
            addModifier(attributeModifier)
        }
    }

    private fun realizeHealth(entity: LivingEntity) {
        var maxHealthValue = formulaManager[entity, MAX_HEALTH]
        if (maxHealthValue < 0) return
        maxHealthValue += if (entity is Player) getSkillAPIHealth(entity).toDouble() else 0.0
        if (isVanillaMaxHealth || entity !is Player)
            realizeAttribute(entity, BukkitAttribute.MAX_HEALTH, maxHealthValue, true)
        else {
            entity.apply {
                getAttribute(BukkitAttribute.MAX_HEALTH)?.apply {
                    clear()
                }
                maxHealthValue += defaultMaxHealth
                maxHealth = if (maxHealthValue < 0.0) return else maxHealthValue
            }
        }
    }


    private fun realizeAll(entity: LivingEntity) {
        realizeHealth(entity)

        val movementSpeed = formulaManager[entity, MOVEMENT_SPEED]
        entity.setWalkSpeed(movementSpeed)

        val knockbackResistance = formulaManager[entity, KNOCKBACK_RESISTANCE]
        realizeAttribute(entity, BukkitAttribute.KNOCKBACK_RESISTANCE, knockbackResistance)

        if (entity !is Player) return
        realizeAttackSpeed(entity)

        val luck = formulaManager[entity, LUCK]
        realizeAttribute(entity, BukkitAttribute.LUCK, luck)
    }

    override fun realize(entity: Entity) {
        if (!entity.isAlive()) return
        entity as LivingEntity
        if (entity is Player) bypassAntiCheat(entity)
        if (isPrimaryThread) {
            realizeAll(entity)
        } else {
            sync { realizeAll(entity) }
        }
        if (entity is Player) recoverAntiCheat(entity)
    }

    private fun realizeAttackSpeed(entity: Player) {
        val value = formulaManager[entity, ATTACK_SPEED]
        entity.getAttribute(BukkitAttribute.ATTACK_SPEED)?.apply {
            if (isVanillaAttackSpeed) {
                if (baseValue != defaultValue)
                    baseValue = defaultValue
                realizeAttribute(entity, BukkitAttribute.ATTACK_SPEED, value, true)
                return
            }
            if (value <= 0.0) {
                if (baseValue != defaultValue)
                    baseValue = defaultValue
                return
            }
            for (modifier in modifiers) {
                removeModifier(modifier)
            }
            baseValue = value
        }
    }

    private fun LivingEntity.setWalkSpeed(value: Double) {
        if (value == -1.0) return
        val result: Double = if (value < 0.0) 0.0 else if (value > 1.0) 1.0 else value
        realizeAttribute(this, BukkitAttribute.MOVEMENT_SPEED, result / 2, isVanillaMovementSpeed)
    }

    private val EntityLivingClass by lazy { nmsClass("EntityLiving") }
    private val CraftLivingEntity by lazy { obcClass("entity.CraftLivingEntity") }

    fun LivingEntity.getAttribute(bukkitAttribute: BukkitAttribute): AttributeInstance? {
        if (MinecraftVersion.majorLegacy <= 11200) {
            val craftAttributes = EntityLivingClass.cast(CraftLivingEntity.cast(this).invokeMethod<Any>("getHandle")!!)
                .getProperty<Any>("craftAttributes") ?: return null
            val attribute = bukkitAttribute.toBukkit()
            return craftAttributes.invokeMethod<AttributeInstance>("getAttribute", attribute)
        } else {
            return this.getAttribute(bukkitAttribute.toBukkit() ?: return null)
        }
    }
    fun AttributeInstance.toFormatString() : String{
       return linkedMapOf("default" to defaultValue,"base" to baseValue, "modifiers" to modifiers.map { it.serialize() }, "total" to value).encodeJson()
    }

    override fun onActive() {
        onReload()
    }

    override fun onReload() {
        healthRegainScheduled?.cancel()
        healthRegainScheduled = createHealthRegainScheduled()
    }

    private fun AttributeInstance.clear() {
        for (modifier in this.modifiers) {
            this.removeModifier(modifier)
        }
    }

    override fun onDisable() {
        AttributeSystem.attributeDataManager.keys.forEach {
            val entity = it.livingEntity() ?: return@forEach
            kotlin.runCatching {
                BukkitAttribute.values().forEach { attribute ->
                    entity.getAttribute(attribute)?.apply {
                        clear()
                        if (attribute == BukkitAttribute.ATTACK_SPEED)
                            baseValue = defaultValue
                    }

                }
            }
        }
    }
}
