package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.AttrAPI
import com.skillw.attsystem.api.operation.Operation
import com.skillw.pouvoir.Pouvoir
import com.skillw.pouvoir.api.manager.ConfigManager
import com.skillw.pouvoir.util.existClass
import com.skillw.pouvoir.util.static
import org.bukkit.Bukkit
import org.spigotmc.AsyncCatcher
import taboolib.common.platform.Platform
import taboolib.common.platform.function.console
import taboolib.common.platform.function.getDataFolder
import taboolib.module.lang.asLangText
import taboolib.module.metrics.charts.SingleLineChart
import java.io.File
import java.util.function.Function
import java.util.regex.Pattern

object ASConfig : ConfigManager(AttributeSystem) {
    override val priority = 0

    val ignores: List<String>
        get() = this["config"].getStringList("options.read.ignores")

    var lineConditionPattern: Pattern = Pattern.compile("options.condition.line-condition.format")

    private val lineConditionFormat: String
        get() = this["config"].getString("options.condition.line-condition.format") ?: ".*\\/(?<requirement>.*)"
    val lineConditionSeparator: String
        get() = this["config"].getString("options.condition.line-condition.separator") ?: ","


    override fun onLoad() {
        AsyncCatcher.enabled = false
        createIfNotExists(
            "dispatchers", "custom-trigger.yml"
        )
        createIfNotExists(
            "handlers", "on-attack.yml"
        )
        createIfNotExists("reader", "number/default.yml", "number/percent.yml", "string/string.yml")
        createIfNotExists(
            "attributes",
            "Example.yml"
        )
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
        )
        //兼容1.4.3及之前的脚本
        mapOf(
            "com.skillw.attsystem.internal.operation.num." to "com.skillw.attsystem.internal.core.operation.num.Operation",
            "com.skillw.attsystem.internal.attribute" to "com.skillw.attsystem.internal.core.attribute",
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
        metrics.addCustomChart(SingleLineChart("conditions") {
            AttributeSystem.conditionManager.size
        })
        Pouvoir.triggerHandlerManager.addSubPouvoir(AttributeSystem)
    }


    override fun subReload() {
        lineConditionPattern = Pattern.compile(lineConditionFormat)
        Pouvoir.scriptManager.addScriptDir(scripts)
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
    val dungeonPlus by lazy {
        Bukkit.getPluginManager().isPluginEnabled("DungeonPlus")
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
    val fightSystem by lazy {
        Bukkit.getPluginManager().isPluginEnabled("FightSystem")
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


    val debug: Boolean
        get() = this["config"].getBoolean("options.debug")
    val numberPattern: String
        get() = this["config"].getString("options.read.number-pattern")
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

    val strAppendSeparator: String
        get() = this["config"].getString("options.operation.string-append-separator") ?: ", "

    @JvmStatic
    fun debug(debug: () -> Unit) {
        if (this.debug) {
            debug.invoke()
        }
    }
}
