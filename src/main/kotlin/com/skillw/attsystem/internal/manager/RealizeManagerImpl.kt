package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.manager.RealizeManager
import com.skillw.attsystem.api.realizer.component.Awakeable
import com.skillw.attsystem.api.realizer.component.Realizable
import com.skillw.attsystem.api.realizer.component.Switchable
import com.skillw.attsystem.api.realizer.component.Sync
import com.skillw.attsystem.util.AntiCheatUtils
import com.skillw.pouvoir.util.isAlive
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common5.FileWatcher
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import taboolib.module.configuration.util.asMap
import java.io.File
import java.util.*

object RealizeManagerImpl : RealizeManager() {
    override val key = "RealizeManager"
    override val priority: Int = 999
    override val subPouvoir = AttributeSystem

    private val awakeables = LinkedList<Awakeable>()
    private val realizables = LinkedList<Realizable>()
    private val syncs = LinkedList<Sync>()

    override fun onLoad() {
        values.filterIsInstance<Realizable>().forEach(realizables::add)
        values.filterIsInstance<Sync>().forEach(syncs::add)
        values.filterIsInstance<Awakeable>().forEach(awakeables::add)
        onReload()
        awakeables.filter { it !is Switchable || it.isEnable() }.forEach(Awakeable::onLoad)
    }

    override fun onEnable() {
        awakeables.filter { it !is Switchable || it.isEnable() }.forEach(Awakeable::onEnable)
    }

    override fun onActive() {
        awakeables.filter { it !is Switchable || it.isEnable() }.forEach(Awakeable::onActive)
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
        awakeables.filter { it !is Switchable || it.isEnable() }.forEach(Awakeable::onReload)
    }

    private fun realizable(func: (Realizable) -> Unit) {
        realizables
            .filter { it !is Switchable || it.isEnable() }
            .forEach(func)
    }

    private val tasks = newKeySet<() -> Unit>()

    private fun genSyncTasks(entity: LivingEntity) {
        syncs
            .filter { it !is Switchable || it.isEnable() }
            .forEach {
                it.newTask(entity)?.let { task -> tasks += task }
            }
    }

    override fun executeSyncTasks() {
        taboolib.common.util.sync { tasks.forEach { it() } }
        tasks.clear()
    }


    override fun realize(entity: LivingEntity) {
        if (!entity.isAlive()) return
        if (entity is Player) AntiCheatUtils.bypassAntiCheat(entity)
        realizable {
            it.realize(entity)
        }
        genSyncTasks(entity)
        if (entity is Player) AntiCheatUtils.recoverAntiCheat(entity)
    }

    override fun unrealize(entity: LivingEntity) {
        if (!entity.isAlive()) return
        realizable {
            it.unrealize(entity)
        }
    }

    @Awake(LifeCycle.DISABLE)
    fun disable() {
        awakeables.filter { it !is Switchable || it.isEnable() }.forEach(Awakeable::onDisable)
    }

    override fun onDisable() {
        awakeables.filter { it !is Switchable || it.isEnable() }.forEach(Awakeable::onDisable)
    }


}
