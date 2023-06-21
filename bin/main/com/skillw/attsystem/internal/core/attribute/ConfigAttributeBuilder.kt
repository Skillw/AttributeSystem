package com.skillw.attsystem.internal.core.attribute

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.attribute.Attribute
import com.skillw.attsystem.api.read.ReadPattern
import com.skillw.attsystem.internal.core.read.ReadGroup
import com.skillw.pouvoir.api.`object`.BaseObject
import com.skillw.pouvoir.util.FileUtils.toMap
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import taboolib.common.platform.function.console
import taboolib.common5.Coerce
import taboolib.module.lang.sendLang
import taboolib.platform.util.sendLang

class ConfigAttributeBuilder(
    override val key: String,
    override val priority: Int,
    private val display: String? = null,
    private val names: List<String>,
    private val readPattern: ReadPattern<*>,
    private val isEntity: Boolean,
    private val map: Map<String, Map<String, String>> = HashMap(),
) : BaseObject {
    override fun register() {
        AttributeSystem.attributeManager.register(
            Attribute.createAttribute(key, readPattern) {
                release = true
                this@ConfigAttributeBuilder.display?.let { display = it }
                priority = this@ConfigAttributeBuilder.priority
                entity = this@ConfigAttributeBuilder.isEntity
                names.addAll(this@ConfigAttributeBuilder.names)
                map = this@ConfigAttributeBuilder.map
            }
        )
    }

    companion object {
        @JvmStatic
        fun deserialize(section: ConfigurationSection): ConfigAttributeBuilder? {
            try {
                val attKey = section.name
                val priority = Coerce.toInteger(section["priority"].toString())
                val display = section["display"]?.toString()
                val names = if (section.contains("names")) section.getStringList("names") else listOf(attKey)
                val isEntity = (section["include-entity"]?.toString()?.lowercase() ?: "true") == "true"
                val readPatternKey =
                    section.getString("read-group")?.lowercase() ?: section.getString("read-pattern")?.lowercase()
                    ?: "default"
                val readPattern =
                    AttributeSystem.readPatternManager[readPatternKey]
                if (readPattern == null) {
                    console().sendLang("invalid-read-pattern", attKey, readPatternKey)
                    return null
                }
                val map = HashMap<String, Map<String, String>>()
                if (readPattern is ReadGroup<*>)
                    section.getConfigurationSection("map")?.getKeys(false)?.forEach {
                        map[it] =
                            section.getConfigurationSection("map.$it")?.toMap()
                                ?.mapValues { (_, value) ->
                                    var temp = value.toString()
                                    readPattern.placeholderKeys.forEach key@{ key ->
                                        if (temp.contains("<$key>", true)) {
                                            temp = temp.replace(
                                                "<$key>",
                                                "%as_att:${attKey}_$key%",
                                                true
                                            )
                                        }
                                    }
                                    temp
                                } ?: return@forEach
                    }
                return ConfigAttributeBuilder(attKey, priority, display, names, readPattern, isEntity, map = map)
            } catch (e: Exception) {
                Bukkit.getConsoleSender().sendLang("error.attribute-load", section["key"].toString())
                e.printStackTrace()
            }
            return null
        }
    }

    override fun serialize(): MutableMap<String, Any> {
        return linkedMapOf(
            "priority" to priority,
            "names" to names,
            "read-group" to readPattern
        )
    }
}
