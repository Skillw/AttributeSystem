package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.attribute.Attribute
import com.skillw.attsystem.api.manager.AttributeManager
import com.skillw.attsystem.internal.core.attribute.ConfigAttributeBuilder
import com.skillw.attsystem.internal.manager.ASConfig.debug
import com.skillw.pouvoir.api.plugin.map.BaseMap
import com.skillw.pouvoir.util.loadMultiply
import taboolib.common.platform.function.console
import taboolib.module.lang.sendLang
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

object AttributeManagerImpl : AttributeManager() {
    override val key = "AttributeManager"
    override val priority: Int = 2
    override val subPouvoir = AttributeSystem

    val nameMap = BaseMap<String, Attribute>()

    override val attrMap = BaseMap<Attribute, BaseMap<String, String>>()

    override val attributes: MutableList<Attribute> by lazy {
        CopyOnWriteArrayList()
    }

    override fun get(key: String): Attribute? {
        return super.get(key) ?: nameMap[key]
    }

    override fun onEnable() {
        onReload()
    }

    override fun onReload() {
        this.entries.filter { it.value.release }.forEach { this.remove(it.key);attrMap.remove(it.value) }
        attributes.removeIf {
            it.release.also { bool ->
                debug {
                    if (bool) console().sendLang(
                        "attribute-unregister",
                        it.display,
                        it.priority
                    )
                }
            }
        }
        this.nameMap.entries.filter { it.value.release }.forEach { nameMap.remove(it.key) }
        loadMultiply(
            File(AttributeSystem.plugin.dataFolder, "attributes"), ConfigAttributeBuilder::class.java
        ).forEach {
            it.key.register()
        }
    }

    override fun put(key: String, value: Attribute): Attribute? {
        attributes.removeIf { it.key == key }
        attributes.add(value)
        attributes.sort()
        value.names.forEach {
            nameMap[it] = value
        }
        debug {
            console().sendLang(
                "attribute-register",
                value.display,
                value.priority
            )
        }
        return super.put(key, value)
    }
}
