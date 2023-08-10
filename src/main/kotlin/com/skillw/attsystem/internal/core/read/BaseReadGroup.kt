package com.skillw.attsystem.internal.core.read

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.attribute.Attribute
import com.skillw.attsystem.api.read.ReadPattern
import com.skillw.attsystem.api.read.operation.Operation
import com.skillw.attsystem.api.read.status.Status
import com.skillw.attsystem.internal.core.read.num.NumberReader
import com.skillw.attsystem.internal.core.read.str.StringReader
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.pouvoir.api.PouvoirAPI.placeholder
import com.skillw.pouvoir.api.plugin.map.LowerMap
import com.skillw.pouvoir.util.format
import com.skillw.pouvoir.util.replacement
import com.skillw.pouvoir.util.toMap
import com.skillw.pouvoir.util.toStringWithNext
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.LivingEntity
import taboolib.common.util.unsafeLazy
import taboolib.module.chat.ComponentText
import taboolib.module.chat.Components
import taboolib.module.chat.colored
import java.util.concurrent.CopyOnWriteArrayList
import java.util.regex.Pattern


/**
 * @className BaseReadGroup
 *
 * @author Glom
 * @date 2022/8/9 11:28 Copyright 2022 user. All rights reserved.
 */


@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseReadGroup<A : Any>(override val key: String) : ReadPattern<A>(key),
    ConfigurationSerializable {
    val placeholderKeys by lazy {
        placeholders.keys
    }
    protected val placeholders = HashMap<String, String>()
    protected val matchers = LowerMap<Matcher<A>>()
    protected val patterns = CopyOnWriteArrayList<PatternMatcher<A>>()

    constructor(
        key: String,
        matchers: Map<String, String>,
        patternStrings: List<String>,
        placeholders: Map<String, String>,
        pattern: String? = null,
    ) : this(key) {
        initMatchers(matchers)
        initPlaceholders(placeholders)
        initPatterns(patternStrings, pattern)
    }

    private fun initMatchers(matchers: Map<String, String>) {
        matchers.forEach { (key, operationStr) ->
            val operation = AttributeSystem.operationManager[operationStr] as? Operation<A>? ?: return@forEach
            val lower = key.lowercase()
            this.matchers.register(lower, Matcher(lower, operation))
        }
    }

    private fun initPlaceholders(placeholders: Map<String, String>) {
        placeholders.forEach {
            var temp = it.value
            placeholders.forEach { (key, value) ->
                if (!temp.contains("<$key>", true) || matchers.containsKey(key)) return@forEach
                temp = temp.replace("<$key>", "($value)", true)
            }
            this.placeholders[it.key] = temp
        }
    }

    private fun initPatterns(patternStrings: List<String>, pattern: String?) {
        patternStrings.forEach { str ->
            var temp = str.replaceFirst("{name}", "\\{name\\}")
            val usedMatchers = HashSet<Matcher<A>>()
            matchers.forEach a@{ (key, matcher) ->
                if (!temp.contains("<$key>", true)) return@a
                if (pattern != null)
                    temp = temp.replace(
                        "<$key>",
                        pattern.replace("value", key.lowercase()),
                        true
                    )
                usedMatchers += matcher
            }
            this.patterns.add(PatternMatcher(Pattern.compile(temp), usedMatchers))
        }
    }

    companion object {
        @JvmStatic
        val keyPattern: Pattern by unsafeLazy {
            Pattern.compile("<(?<key>.*?)>")
        }

        @JvmStatic
        fun deserialize(section: ConfigurationSection): BaseReadGroup<*> {
            val key = section.name
            val matchers =
                (section.getConfigurationSection("matchers")?.toMap() ?: emptyMap()).mapValues { it.value.toString() }
            val patternStrings = section.getStringList("patterns")
            val placeholders = (section.getConfigurationSection("placeholders")?.toMap()
                ?: emptyMap()).mapValues { it.value.toString() }
            return when (section.getString("type")) {
                "number" -> NumberReader(key, matchers, patternStrings, placeholders)
                "string" -> StringReader(key, matchers, patternStrings, placeholders)
                else -> error("Unknown group type: ${section.getString("type")}")
            }
        }
    }

    override fun operations() = matchers as LowerMap<Operation<A>>

    override fun serialize(): MutableMap<String, Any> {
        return linkedMapOf(
            "key" to key,
            "matchers" to matchers,
            "patternStrings" to patterns.map { it.pattern.pattern() },
            "placeholders" to placeholders
        )
    }

    private fun Any?.formatStr(): String = if (this is Number) format("#.###") else toString()

    override fun stat(attribute: Attribute, status: Status<*>, entity: LivingEntity?): ComponentText {
        val json = Components.empty()

        val statusStr = status
            .map { ASConfig.statusValue.replacement(mapOf("{key}" to it.key, "{value}" to it.value.formatStr())) }
            .ifEmpty { listOf(ASConfig.statusNone) }
            .toStringWithNext()

        val placeholderStr = placeholders.keys.map {
            ASConfig.statusPlaceholderValue.replacement(
                mapOf(
                    "{key}" to it,
                    "{value}" to placeholder(it, attribute, status, entity).formatStr()
                )
            )
        }.ifEmpty { listOf(ASConfig.statusNone) }.toStringWithNext()

        json.append(
            ASConfig.statusAttributeFormat.replacement(
                mapOf(
                    "{name}" to attribute.display,
                    "{value}" to getTotal(attribute, status, entity).formatStr()
                )
            ).colored()
        ).hoverText(
            "${ASConfig.statsStatus}\n$statusStr\n\n${ASConfig.statusPlaceholder}\n$placeholderStr".trimIndent()
                .colored()
        )
        return json
    }

    protected fun replacePlaceholder(
        key: String,
        status: Status<A>,
        entity: LivingEntity?,
    ): String? {
        val formula = placeholders[key] ?: return null
        val matcher = keyPattern.matcher(formula)
        val stringBuffer = StringBuffer()
        while (matcher.find()) {
            val matcherKey = matcher.group("key") ?: continue
            val replaced = status[matcherKey] ?: 0.0
            matcher.appendReplacement(stringBuffer, replaced.toString())
        }
        return matcher.appendTail(stringBuffer).toString().run { entity?.let { placeholder(it) } ?: this }
    }


    protected abstract fun onPlaceholder(
        key: String,
        attribute: Attribute,
        status: Status<A>,
        entity: LivingEntity?,
    ): A?

    override fun placeholder(key: String, attribute: Attribute, status: Status<*>, entity: LivingEntity?): A? {
        return onPlaceholder(key, attribute, status as? Status<A>? ?: return null, entity)
    }


}