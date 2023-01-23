package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.manager.RealizeManager
import com.skillw.attsystem.api.realizer.component.ConfigComponent
import com.skillw.attsystem.api.realizer.component.IConfigComponent
import com.skillw.attsystem.api.realizer.component.sub.Awakeable
import com.skillw.attsystem.api.realizer.component.sub.Realizable
import com.skillw.attsystem.api.realizer.component.sub.Switchable
import com.skillw.attsystem.api.realizer.component.sub.Syncable
import com.skillw.attsystem.util.AntiCheatUtils
import com.skillw.pouvoir.api.plugin.handler.ClassHandler
import com.skillw.pouvoir.taboolib.library.reflex.ClassStructure
import com.skillw.pouvoir.util.isAlive
import com.skillw.pouvoir.util.listSubFiles
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.io.newFile
import taboolib.common.platform.function.isPrimaryThread
import taboolib.common.util.sync
import taboolib.common.util.unsafeLazy
import taboolib.library.reflex.Reflex.Companion.invokeMethod
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import taboolib.module.configuration.util.asMap

object RealizeManagerImpl : RealizeManager() {
    override val key = "RealizeManager"
    override val priority: Int = 9
    override val subPouvoir = AttributeSystem
    private val components = HashSet<Class<*>>()
    private val configComponents by unsafeLazy {
        components.filter { IConfigComponent::class.java.isAssignableFrom(it) }.toHashSet()
    }

    object ConfigComponentInject : ClassHandler(0) {
        override fun inject(clazz: ClassStructure) {
            if (clazz.owner.isAnnotationPresent(ConfigComponent::class.java))
                components.add(clazz.owner)
        }
    }

    override fun onLoad() {
        onReload()
        values.filterIsInstance<Awakeable>().forEach(Awakeable::onLoad)
    }

    override fun onEnable() {
        values.filterIsInstance<Awakeable>().forEach(Awakeable::onEnable)
    }

    override fun onReload() {

        val filenames = HashSet<String>()
        val previous = HashMap<String, Boolean>()
        values.forEach { realizer ->
            val key = realizer.key
            val fileName = realizer.fileName
            previous[key] = realizer !is Switchable || realizer.isEnable()
            filenames.add(fileName)
            val defaultConfig = realizer.defaultConfig.clone() as MutableMap<String, Any>
            configComponents.filter { it.isAssignableFrom(realizer::class.java) }
                .forEach {
                    it.invokeMethod<Unit>("defaultConfig", realizer, defaultConfig, isStatic = true)
                }
            val file = newFile(AttributeSystem.plugin.dataFolder, fileName)
            val config = Configuration.loadFromFile(file, Type.YAML)
            config[key] = if (config.isConfigurationSection(key)) {
                config.getConfigurationSection(key)?.also { innerConfig ->
                    defaultConfig.forEach { (subKey, value) ->
                        if (!innerConfig.contains(subKey))
                            innerConfig[subKey] = value
                    }
                }
            } else defaultConfig
            config.saveToFile(file)
        }
        AttributeSystem.plugin.dataFolder.listSubFiles().filter {
            it.extension == "yml"
        }.map {
            Configuration.loadFromFile(it, Type.YAML)
        }.filter { it.file!!.name in filenames }
            .forEach {
                it.toMap().forEach { (key, data) ->
                    this[key]?.config?.apply {
                        clear()
                        putAll(data.asMap().entries.associate { entry -> entry.key to entry.value!! })
                    }
                }
            }
        values.forEach { realizer ->
            if (realizer is Switchable) {
                val now = realizer.isEnable()
                if (now == previous[realizer.key]) return@forEach
                if (now) realizer.whenEnable()
                else realizer.whenDisable()
            }
        }
        values.filterIsInstance<Awakeable>().forEach(Awakeable::onReload)
    }

    override fun realize(entity: LivingEntity) {
        if (!entity.isAlive()) return
        if (entity is Player) AntiCheatUtils.bypassAntiCheat(entity)
        if (!isPrimaryThread) {
            //不同步处理一些东西会死的
            sync {
                values
                    .filterIsInstance<Realizable>()
                    .filter { it !is Switchable || it.isEnable() }
                    .filterIsInstance<Syncable>()
                    .forEach {
                        (it as Realizable).realize(entity)
                    }
            }
            values
                .filterIsInstance<Realizable>()
                .filter { it !is Switchable || it.isEnable() }
                .filter { it !is Syncable }
                .forEach {
                    it.realize(entity)
                }
        } else {
            values
                .filterIsInstance<Realizable>()
                .filter { it !is Switchable || it.isEnable() }
                .forEach {
                    it.realize(entity)
                }
        }
        if (entity is Player) AntiCheatUtils.recoverAntiCheat(entity)
    }

    override fun unrealize(entity: LivingEntity) {
        if (!entity.isAlive()) return
        values
            .filterIsInstance<Realizable>()
            .forEach {
                it.unrealize(entity)
            }
    }

    override fun onDisable() {
        values.filterIsInstance<Awakeable>().forEach(Awakeable::onDisable)
    }
}
