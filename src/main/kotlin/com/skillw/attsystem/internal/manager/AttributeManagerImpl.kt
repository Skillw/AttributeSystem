package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.attribute.Attribute
import com.skillw.attsystem.api.event.AttributeRegisterEvent
import com.skillw.attsystem.api.manager.AttributeManager
import com.skillw.attsystem.internal.core.attribute.ConfigAttributeBuilder
import com.skillw.attsystem.internal.manager.ASConfig.debug
import com.skillw.pouvoir.api.plugin.SubPouvoir
import com.skillw.pouvoir.api.plugin.map.BaseMap
import com.skillw.pouvoir.api.plugin.map.LowerMap
import com.skillw.pouvoir.util.loadMultiply
import com.skillw.pouvoir.util.loadYaml
import com.skillw.pouvoir.util.put
import com.skillw.pouvoir.util.safe
import taboolib.common.platform.function.console
import taboolib.common5.FileWatcher
import taboolib.module.lang.sendLang
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

object AttributeManagerImpl : AttributeManager() {
    override val key = "AttributeManager"
    override val priority: Int = 2
    override val subPouvoir = AttributeSystem
    private val fileWatcher = FileWatcher()
    private val dataFolders = HashSet<File>()
    private val fileToKeys = BaseMap<File, HashSet<String>>()
    private val folderToKeys = BaseMap<File, HashSet<String>>()

    override val nameMap = LowerMap<Attribute>()

    override val attributes: MutableList<Attribute> by lazy {
        CopyOnWriteArrayList()
    }


    override fun addSubPouvoir(subPouvoir: SubPouvoir) {
        val folder = subPouvoir.plugin.dataFolder
        addDataFolders(folder)
        subPouvoir.managerData.onReload {
            reloadFolder(folder)
        }
    }

    override fun onEnable() {
        addSubPouvoir(AttributeSystem)
        onReload()
    }

    override fun reloadFolder(folder: File) {
        dataFolders.add(folder)
        folderToKeys[folder]?.forEach(::unregister)
        loadMultiply(
            File(folder, "attributes"), ConfigAttributeBuilder::class.java
        ).forEach {
            val (builder, file) = it
            safe { builder.register() }
            fileToKeys.put(file, builder.key)
            folderToKeys.put(folder, builder.key)
            if (!fileWatcher.hasListener(file)) {
                fileWatcher.addSimpleListener(file) {
                    reloadFile(file)
                }
            }
        }
    }

    private fun reloadFile(file: File) {
        fileToKeys[file]?.let {
            it.forEach(::unregister)
            fileToKeys.remove(file)
            val yaml = runCatching { file.loadYaml() }.getOrNull() ?: return
            yaml.apply {
                getKeys(false).forEach { key ->
                    ConfigAttributeBuilder.deserialize(getConfigurationSection(key)!!)?.register()
                    fileToKeys.put(file, key)
                }
            }
        }
    }

    private fun refreshFileListener(todo: () -> Unit) {
        fileToKeys.keys.forEach(fileWatcher::removeListener)
        fileToKeys.clear()
        todo()
        fileToKeys.keys.forEach { file ->
            fileWatcher.addSimpleListener(file) {
                reloadFile(file)
            }
        }
    }

    override fun addDataFolders(folder: File) {
        dataFolders.add(folder)
        onReload()
    }

    override fun onReload() {
        this.entries.filter { it.value.config }.forEach { this.remove(it.key); }
        attributes.removeIf { it.config }
        this.nameMap.entries.filter { it.value.config }.forEach { nameMap.remove(it.key) }
        refreshFileListener {
            dataFolders.forEach(::reloadFolder)
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
        AttributeRegisterEvent(value).call()
        return super.put(key, value)
    }

    override fun unregister(key: String) {

        remove(key)?.apply {
            names.forEach(nameMap::remove)
            debug {
                console().sendLang(
                    "attribute-unregister",
                    display,
                    priority
                )
            }
        }
    }

    override operator fun get(key: String): Attribute? {
        val lower = key.lowercase()
        return super.get(lower) ?: nameMap[lower]
    }
}
