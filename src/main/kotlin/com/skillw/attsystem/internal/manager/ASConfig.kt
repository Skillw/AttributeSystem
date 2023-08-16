package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.AttrAPI
import com.skillw.pouvoir.Pouvoir
import com.skillw.pouvoir.api.feature.operation.Operation
import com.skillw.pouvoir.api.manager.ConfigManager
import com.skillw.pouvoir.api.plugin.map.DataMap
import com.skillw.pouvoir.util.static
import com.skillw.pouvoir.util.toMap
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
        get() = this["config"].getString("options.condition.line-condition.format") ?: "\\/(?<requirement>.*)"
    val lineConditionSeparator: String
        get() = this["config"].getString("options.condition.line-condition.separator") ?: ","

    val databaseConfig: DataMap
        get() = DataMap().also { it.putAll(this["config"].getConfigurationSection("database")!!.toMap()) }


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
            "conditions/slot.js",
            "conditions/attribute.js",
        )
        //兼容2.1.0-beta及之前的脚本
        mapOf(
            "com.skillw.attsystem.internal.core.operation.num.Operation" to "com.skillw.pouvoir.api.feature.operation.Operation"
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
        Pouvoir.triggerHandlerManager.addSubPouvoir(AttributeSystem)
    }


    override fun subReload() {
        lineConditionPattern = Pattern.compile(lineConditionFormat)
        Pouvoir.scriptManager.addScriptDir(scripts)
        completeYaml("config.yml")
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

    val skillAPI by lazy {
        Bukkit.getPluginManager().isPluginEnabled("SkillAPI") || Bukkit.getPluginManager()
            .isPluginEnabled("ProSkillAPI")
    }
    val fightSystem by lazy {
        Bukkit.getPluginManager().isPluginEnabled("FightSystem")
    }

    private val scripts = File(getDataFolder(), "scripts")

    var debugMode: Boolean = false

    val debug: Boolean
        get() = debugMode || this["config"].getBoolean("options.debug")
    const val numberPattern: String = "(?<value>(\\+|\\-)?(\\d+(?:(\\.\\d+))?))"

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


    @JvmStatic
    fun debug(debug: () -> Unit) {
        if (this.debug) {
            debug.invoke()
        }
    }
}
