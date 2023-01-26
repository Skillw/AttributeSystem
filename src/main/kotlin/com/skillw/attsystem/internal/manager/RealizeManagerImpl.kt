package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.manager.RealizeManager
import com.skillw.attsystem.api.realizer.component.sub.Awakeable
import com.skillw.attsystem.api.realizer.component.sub.Realizable
import com.skillw.attsystem.api.realizer.component.sub.Switchable
import com.skillw.attsystem.api.realizer.component.sub.Syncable
import com.skillw.attsystem.util.AntiCheatUtils
import com.skillw.pouvoir.util.isAlive
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.platform.function.isPrimaryThread
import taboolib.common.util.sync
import taboolib.common5.FileWatcher
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import taboolib.module.configuration.util.asMap
import java.io.File

object RealizeManagerImpl : RealizeManager() {
    override val key = "RealizeManager"
    override val priority: Int = 9
    override val subPouvoir = AttributeSystem

    override fun onLoad() {
        onReload()
        values.filterIsInstance<Awakeable>().forEach(Awakeable::onLoad)
    }

    override fun onEnable() {
        values.filterIsInstance<Awakeable>().forEach(Awakeable::onEnable)
    }

    private val watcher = FileWatcher()

    override fun onReload() {
        val files = HashSet<File>()
        val previous = HashMap<String, Boolean>()
        values.forEach { realizer ->
            val key = realizer.key
            val file = realizer.file
            previous[key] = realizer !is Switchable || realizer.isEnable()
            files.add(file)
        }
        files.map {
            Configuration.loadFromFile(it, Type.YAML)
        }.forEach {
            watcher.removeListener(it.file)
            it.toMap().forEach inner@{ (key, data) ->
                val realizer = this[key]
                if (realizer?.file?.path != it.file?.path) return@inner
                realizer?.config?.apply {
                    clear()
                    putAll(data.asMap().entries.associate { entry -> entry.key to entry.value!! })
                }
            }
            watcher.addSimpleListener(it.file) {
                Configuration.loadFromFile(it.file!!, Type.YAML).toMap().forEach { (key, data) ->
                    this[key]?.config?.apply {
                        clear()
                        putAll(data.asMap().entries.associate { entry -> entry.key to entry.value!! })
                    }
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
        if (!isPrimaryThread) {
            //不同步处理一些东西会死的
            sync {
                values
                    .filterIsInstance<Realizable>()
                    .filter { it !is Switchable || it.isEnable() }
                    .filterIsInstance<Syncable>()
                    .forEach {
                        (it as Realizable).unrealize(entity)
                    }
            }
            values
                .filterIsInstance<Realizable>()
                .filter { it !is Switchable || it.isEnable() }
                .filter { it !is Syncable }
                .forEach {
                    it.unrealize(entity)
                }
        } else {
            values
                .filterIsInstance<Realizable>()
                .filter { it !is Switchable || it.isEnable() }
                .forEach {
                    it.unrealize(entity)
                }
        }
    }

    override fun onDisable() {
        values.filterIsInstance<Awakeable>().forEach(Awakeable::onDisable)
    }
}
