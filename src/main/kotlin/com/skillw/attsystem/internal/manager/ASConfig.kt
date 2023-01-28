package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.AttrAPI
import com.skillw.attsystem.api.operation.Operation
import com.skillw.pouvoir.Pouvoir
import com.skillw.pouvoir.api.manager.ConfigManager
import com.skillw.pouvoir.api.map.BaseMap
import com.skillw.pouvoir.util.ClassUtils.existClass
import com.skillw.pouvoir.util.ClassUtils.static
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.spigotmc.AsyncCatcher
import taboolib.common.platform.Platform
import taboolib.common.platform.function.console
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.submit
import taboolib.library.xseries.XMaterial
import taboolib.module.lang.asLangText
import taboolib.module.metrics.charts.SingleLineChart
import java.io.File
import java.util.*
import java.util.function.Function
import java.util.regex.Pattern

object ASConfig : ConfigManager(AttributeSystem) {
    override val priority = 0

    val ignores: List<String>
        get() = this["config"].getStringList("options.attribute.ignores")

    var lineConditionPattern: Pattern = Pattern.compile("options.attribute.line-condition.format")

    private val lineConditionFormat: String
        get() = this["config"].getString("options.attribute.line-condition.format") ?: ".*\\/(?<requirement>.*)"
    val lineConditionSeparator: String
        get() = this["config"].getString("options.attribute.line-condition.separator") ?: ","

    val attributeClearSchedule: Long
        get() =
            this["config"].getLong("options.attribute.time.attribute-clear")

    override fun onLoad() {
        AsyncCatcher.enabled = false
        createIfNotExists("formula", "example.yml")
        createIfNotExists(
            "attributes",
            "Fight/Physical.yml",
            "Fight/Magic.yml",
            "Fight/Other.yml",
            "Mechanic/Vampire.yml",
            "Mechanic/Mechanic.yml",
            "Other/Other.yml",
            "Example.yml",
            "shield.yml"
        )
        createIfNotExists("reader", "number/default.yml", "number/percent.yml", "string/string.yml")
        createIfNotExists("fight_group", "default.yml", "skapi.yml", "mythic_skill.yml", "damage_event.yml")
        createIfNotExists("damage_type", "magic.yml", "physical.yml", "real.yml")
        createIfNotExists(
            "scripts",
            "conditions/level.js",
            "conditions/permission.js",
            "conditions/altitude.js",
            "conditions/fighting.js",
            "conditions/food.js",
            "conditions/water.js",
            "conditions/health.js",
            "conditions/world.js",
            "conditions/slot.js",
            "conditions/fire.js",
            "conditions/weather.js",
            "conditions/ground.js",
            "conditions/attribute.js",
            "listeners/arrow.js",
            "listeners/exp.js",
            "listeners/mana.js",
            "listeners/cooldown.js",
            "mechanics/basic.js",
            "mechanics/mechanics.js",
            "mechanics/mythicskill.js",
            "mechanics/runner.js",
            "mechanics/shield.js",
            "operation/str_roman.js"
        )
        //兼容1.4.3及之前的脚本
        mapOf(
            "com.skillw.attsystem.internal.operation.num." to "com.skillw.attsystem.internal.core.operation.num.Operation",
            "com.skillw.attsystem.internal.attribute" to "com.skillw.attsystem.internal.core.attribute",
            "com.skillw.attsystem.internal.fight" to "com.skillw.attsystem.internal.core.fight",
            "com.skillw.attsystem.internal.read" to "com.skillw.attsystem.internal.core.read"
        ).forEach(Pouvoir.scriptEngineManager::relocate)

        Pouvoir.scriptEngineManager.globalVariables.let {
            it["AttrAPI"] = AttrAPI::class.java.static()
            it["AttributeSystem"] = AttributeSystem::class.java.static()
            it["operation"] = Function<String, Operation<*>> { name ->
                AttrAPI.operation(name)
            }
        }
    }

    override fun onEnable() {
        onReload()
        val metrics =
            taboolib.module.metrics.Metrics(14465, AttributeSystem.plugin.description.version, Platform.BUKKIT)
        metrics.addCustomChart(SingleLineChart("attributes") {
            AttributeSystem.attributeManager.attributes.size
        })
        metrics.addCustomChart(SingleLineChart("read_patterns") {
            AttributeSystem.readPatternManager.size
        })
        metrics.addCustomChart(SingleLineChart("fight_groups") {
            AttributeSystem.fightGroupManager.size
        })
        metrics.addCustomChart(SingleLineChart("conditions") {
            AttributeSystem.conditionManager.size
        })
        metrics.addCustomChart(SingleLineChart("mechanics") {
            AttributeSystem.mechanicManager.size
        })
    }


    val attackFightKeyMap = BaseMap<String, String>()
    override fun subReload() {
        lineConditionPattern = Pattern.compile(lineConditionFormat)
        Pouvoir.scriptManager.addScriptDir(scripts)
        submit {
            val scale: Int = healthScale
            if (scale != -1) {
                Bukkit.getServer().onlinePlayers.forEach { player: Player ->
                    player.isHealthScaled = true
                    player.healthScale = scale.toDouble()
                }
            } else {
                Bukkit.getServer().onlinePlayers.forEach { player: Player -> player.isHealthScaled = false }
            }
        }
        disableDamageTypes.clear()
        for (material in this["config"].getStringList("options.fight.disable-damage-types")) {
            val xMaterial = XMaterial.matchXMaterial(material)
            if (xMaterial.isPresent) {
                disableDamageTypes.add(xMaterial.get().parseMaterial() ?: continue)
            } else {
                val materialMC = Material.matchMaterial(material)
                disableDamageTypes.add(materialMC ?: continue)
            }
        }
        disableCooldownTypes.clear()
        for (material in this["config"].getStringList("options.fight.attack-speed.no-cooldown-types")) {
            val xMaterial = XMaterial.matchXMaterial(material)
            if (xMaterial.isPresent) {
                disableCooldownTypes.add(xMaterial.get().parseMaterial() ?: continue)
            } else {
                val materialMC = Material.matchMaterial(material)
                disableCooldownTypes.add(materialMC ?: continue)
            }
        }
        attackFightKeyMap.clear()
        this["config"].getConfigurationSection("options.fight.attack-fight")?.apply {
            getKeys(false).forEach { key: String ->
                attackFightKeyMap[key] = getString(key) ?: return@forEach
            }
        }
    }

    val germSlots: List<String>
        get() {
            return this["slot"].getStringList("germ-slots")
        }
    val germ by lazy {
        Bukkit.getPluginManager().isPluginEnabled("GermPlugin")
    }
    val dragonCore by lazy {
        Bukkit.getPluginManager().isPluginEnabled("DragonCore")
    }
    val matrix by lazy {
        Bukkit.getPluginManager().isPluginEnabled("Matrix")
    }
    val aac by lazy {
        Bukkit.getPluginManager().isPluginEnabled("AAC5")
    }
    val skillAPI by lazy {
        Bukkit.getPluginManager().isPluginEnabled("SkillAPI") || Bukkit.getPluginManager()
            .isPluginEnabled("ProSkillAPI")
    }

    val mythicMobs by lazy {
        Bukkit.getPluginManager().isPluginEnabled("MythicMobs")
    }

    val mythicMobsIV by lazy {
        mythicMobs && "io.lumine.xikage.mythicmobs.MythicMobs".existClass()
    }
    val mythicMobsV by lazy {
        mythicMobs && "io.lumine.mythic.bukkit.MythicBukkit".existClass()
    }

    private val scripts = File(getDataFolder(), "scripts")
    val isPersonalEnable
        get() = this["message"].getBoolean("options.personal")

    val defaultAttackMessageType: String
        get() = (this["message"].getString("options.default.attack") ?: "HOLO").uppercase()

    val defaultDefendMessageType: String
        get() = (this["message"].getString("options.default.defend") ?: "CHAT").uppercase()

    val defaultRegainHolo: Boolean
        get() = this["message"].getBoolean("options.default.health-regain-holo")


    val disableRegainOnFight: Boolean
        get() = this["config"].getBoolean("options.fight.disable-regain-on-fight")


    val enableCooldown: Boolean
        get() = this["config"].getString("options.fight.attack-speed.type")?.lowercase().equals("cooldown")


    val isAttackAnyTime: Boolean
        get() = this["config"].getBoolean("options.fight.attack-speed.damage-any-time")


    val isVanillaMaxHealth: Boolean
        get() = this["config"].getBoolean("options.fight.vanilla-max-health")
    val isVanillaMovementSpeed: Boolean
        get() = this["config"].getBoolean("options.fight.vanilla-movement-speed")
    val isVanillaAttackSpeed: Boolean
        get() = this["config"].getBoolean("options.fight.vanilla-attack-speed")
    val isVanillaArmor: Boolean
        get() = this["config"].getBoolean("options.fight.vanilla-armor")
    val isVanillaRegain: Boolean
        get() = this["config"].getBoolean("options.fight.vanilla-regain")
    val combatValueScript: String?
        get() = this["config"].getString("option.attribute.combat-value")
    val fightStatusTime: Long
        get() = this["config"].getLong("options.fight.fight-status.time")


    val healthScale: Int
        get() = this["config"].getInt("options.health-scale")

    val noDamageTicks: Int
        get() = this["config"].getInt("options.fight.no-damage-ticks.value")
    val defaultDistance: Double
        get() = this["config"].getDouble("options.fight.vanilla-distance.default")
    val creativeDistance: Double
        get() = this["config"].getDouble("options.fight.vanilla-distance.creative")

    val noDamageTicksDisableWorlds: List<String>
        get() = this["config"].getStringList("options.fight.no-damage-ticks.disable-world")


    val debug: Boolean
        get() = this["config"].getBoolean("options.debug")

    val healthRegainSchedule
        get() = this["config"].getLong("options.attribute.time.health-regain")

    val forceBasedCooldown
        get() = this["config"].getBoolean("options.fight.attack-speed.force-based-cooldown")
    val isAttackForce
        get() = this["config"].getBoolean("options.fight.attack-speed.attack-force")
    val minForce
        get() = this["config"].getDouble("options.fight.attack-speed.min-force")

    val isDistanceEffect
        get() = this["config"].getBoolean("options.fight.distance-attack.effect")
    val isDistanceSound
        get() = this["config"].getBoolean("options.fight.distance-attack.sound")


    val disableDamageTypes = LinkedList<Material>()
    val disableCooldownTypes = LinkedList<Material>()

    //na

    val numberPattern: String
        get() = this["config"].getString("options.number-pattern")
            ?: "(?<value>(\\\\+|\\\\-)?(\\\\d+(?:(\\\\.\\\\d+))?))"

    val statsTitle: String
        get() = console().asLangText("stats-title")
    val statsStatus
        get() = console().asLangText("stats-status")
    val statusAttributeFormat
        get() = console().asLangText("stats-attribute-format")

    val statusNone
        get() = console().asLangText("stats-status-none")

    val statusValue
        get() = console().asLangText("stats-status-value")

    val statusPlaceholder
        get() = console().asLangText("stats-status-placeholder")

    val statusPlaceholderValue
        get() = console().asLangText("stats-status-placeholder-value")

    val statsEnd: String
        get() = console().asLangText("stats-end")

    val eveFightCal
        get() = this["config"].getBoolean("options.fight.eve-fight-cal")
    val strAppendSeparator: String
        get() = this["config"].getString("options.string-append-separator") ?: ", "

    val defaultAttackerName: String
        get() = this["config"].getString("fight-message.default-name.attacker") ?: "大自然"
    val defaultDefenderName: String
        get() = this["config"].getString("fight-message.default-name.defender") ?: "未知"

    val arrowCache
        get() = this["config"].getBoolean("options.fight.arrow-cache-data", true)

    @JvmStatic
    fun debug(debug: () -> Unit) {
        if (this.debug) {
            debug.invoke()
        }
    }
}
