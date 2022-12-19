package com.skillw.attsystem.internal.core.read

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.attribute.Attribute
import com.skillw.attsystem.api.operation.Operation
import com.skillw.attsystem.api.read.ReadPattern
import com.skillw.attsystem.api.status.GroupStatus
import com.skillw.attsystem.api.status.Status
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.attsystem.internal.core.read.num.NumberReader
import com.skillw.attsystem.internal.core.read.str.StringReader
import com.skillw.pouvoir.api.PouvoirAPI.placeholder
import com.skillw.pouvoir.api.map.LowerKeyMap
import com.skillw.pouvoir.util.FileUtils.toMap
import com.skillw.pouvoir.util.StringUtils.replacement
import com.skillw.pouvoir.util.StringUtils.toStringWithNext
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.LivingEntity
import taboolib.common.util.unsafeLazy
import taboolib.common5.Coerce
import taboolib.module.chat.TellrawJson
import taboolib.module.chat.colored
import java.util.concurrent.CopyOnWriteArrayList
import java.util.regex.Pattern


/**
 * @className ReadGroup
 *
 * @author Glom
 * @date 2022/8/9 11:28 Copyright 2022 user. All rights reserved.
 */


abstract class ReadGroup<A : Any>(
    override val key: String,
    matchers: Map<String, String>,
    patternStrings: List<String>,
    protected val placeholders: Map<String, String>,
    defaultPattern: String? = null,
) : ReadPattern<A>(key),
    ConfigurationSerializable {
    val placeholderKeys = placeholders.keys

    val matchers = LowerKeyMap<Matcher<A>>().apply {
        matchers.forEach { (key, operationStr) ->
            val operation = AttributeSystem.operationManager[operationStr] as? Operation<A>? ?: return@forEach
            register(Matcher(key.lowercase(), operation))
        }
    }
    protected val patterns: MutableList<PatternMatcher<A>> =
        CopyOnWriteArrayList<PatternMatcher<A>>().apply {
            patternStrings.forEach { str ->
                var temp = str.replace("{name}", "\\{name\\}")
                val nMatchers = HashSet<Matcher<A>>()
                this@ReadGroup.matchers.forEach matcher@{ (key, matcher) ->
                    if (temp.contains("<$key>", true)) {
                        defaultPattern?.let {
                            temp = temp.replace("<$key>", it.replace("value", key.lowercase()), true)
                        }
                        nMatchers += matcher
                    }
                }
                add(PatternMatcher(Pattern.compile(temp), nMatchers))
            }
        }

    companion object {
        @JvmStatic
        val keyPattern by unsafeLazy {
            Pattern.compile("<(?<key>.*?)>")
        }

        @JvmStatic
        fun deserialize(section: ConfigurationSection): ReadGroup<*> {
            val key = section.name
            val matchers =
                (section.getConfigurationSection("matchers")?.toMap() ?: emptyMap()).mapValues { it.value.toString() }
            val patternStrings = section.getStringList("patterns")
            val placeholders = (section.getConfigurationSection("placeholders")?.toMap()
                ?: emptyMap()).mapValues { it.value.toString() }
            return when (section.getString("type")) {
                "number" -> NumberReader(key, matchers, patternStrings, placeholders)
                "string" -> StringReader(key, matchers, patternStrings, placeholders)
                else -> throw IllegalArgumentException("Unknown group type: ${section.getString("type")}")
            }
        }
    }

    override fun serialize(): MutableMap<String, Any> {
        return linkedMapOf(
            "key" to key,
            "matchers" to matchers,
            "patternStrings" to patterns.map { it.pattern.pattern() },
            "placeholders" to placeholders
        )
    }

    override fun stat(attribute: Attribute, status: Status<*>, entity: LivingEntity?): TellrawJson {
        val json = TellrawJson()
        if (status !is GroupStatus<*>) return json
        val statusStr = status.map {
            ASConfig.statusValue.replacement(
                mapOf(
                    "{key}" to it.key,
                    "{value}" to it.value
                )
            )
        }.ifEmpty { listOf(ASConfig.statusNone) }.toStringWithNext()
        val placeholderStr = placeholders.keys.map {
            ASConfig.statusPlaceholderValue.replacement(
                mapOf(
                    "{key}" to it,
                    "{value}" to placeholder(it, attribute, status, entity).toString()
                )
            )
        }.ifEmpty { listOf(ASConfig.statusNone) }.toStringWithNext()
        json.append(
            ASConfig.statusAttributeFormat.replacement(
                mapOf(
                    "{name}" to attribute.display,
                    "{value}" to placeholder("total", attribute, status, entity).toString()
                )
            )
                .colored()
        ).hoverText(
            ("${ASConfig.statsStatus} \n" +
                    statusStr
                    + "\n \n"
                    + "${ASConfig.statusPlaceholder} \n"
                    + placeholderStr).colored()
        )
        return json
    }

    protected fun replacePlaceholder(key: String, status: GroupStatus<A>, entity: LivingEntity?): String? {
        val formula = placeholders[key] ?: return null
        val matcher = keyPattern.matcher(formula)
        val stringBuffer = StringBuffer()
        while (matcher.find()) {
            val matcherKey = matcher.group("key") ?: continue
            matcher.appendReplacement(stringBuffer, status[matcherKey].toString())
        }
        return matcher.appendTail(stringBuffer).toString().run { entity?.let { placeholder(it) } ?: this }
    }


    protected abstract fun onPlaceholder(
        key: String,
        attribute: Attribute,
        status: GroupStatus<A>,
        entity: LivingEntity?,
    ): A?

    override fun placeholder(key: String, attribute: Attribute, status: Status<*>, entity: LivingEntity?): A? {
        return onPlaceholder(key, attribute, status as? GroupStatus<A>? ?: return null, entity)
    }
}