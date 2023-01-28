package com.skillw.attsystem.internal.feature.realizer

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.realizer.BaseRealizer
import com.skillw.attsystem.api.realizer.component.sub.*
import com.skillw.attsystem.util.AttributeUtils.clear
import com.skillw.attsystem.util.AttributeUtils.getAttribute
import com.skillw.attsystem.util.BukkitAttribute
import org.bukkit.Bukkit
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import java.util.*
import java.util.concurrent.ConcurrentHashMap

open class VanillaRealizer(key: String, val attribute: BukkitAttribute) : BaseRealizer(key), Realizable, Awakeable,
    Switchable, Vanillable, Valuable, Syncable {
    override val file by lazy {
        AttributeSystem.vanilla.file!!
    }
    protected val realizeKey = "realizer-vanilla-$key"

    override val defaultEnable: Boolean
        get() = false
    override val defaultValue: String
        get() = "0"
    override val defaultVanilla: Boolean
        get() = true

    protected val cache = ConcurrentHashMap<UUID, AttributeModifier>()

    override fun realize(entity: LivingEntity) {
        val uuid = entity.uniqueId
        entity.getAttribute(attribute)?.run {
            cache[uuid]?.let {
                removeModifier(it)
            }
            if (!isEnableVanilla()) clear()
            addModifier(
                AttributeModifier(
                    realizeKey,
                    value(entity),
                    AttributeModifier.Operation.ADD_NUMBER
                ).also {
                    cache[uuid] = it
                }
            )
        }
    }

    override fun unrealize(entity: LivingEntity) {
        val uuid = entity.uniqueId
        entity.getAttribute(attribute)?.run {
            cache[uuid]?.let {
                removeModifier(it)
            }
        }
    }

    companion object {
        private val BukkitAttribute.normalizeName
            get() = name.lowercase().replace("_", "-")

        @Awake(LifeCycle.LOAD)
        fun registerCommon() {
            MaxHealthRealizer.register()
            BukkitAttribute.values().filter { it != BukkitAttribute.MAX_HEALTH }.forEach { att ->
                att.toBukkit()?.let { VanillaRealizer(att.normalizeName, att).register() }
            }
        }
    }


    override fun whenDisable() {
        onDisable()
    }

    override fun onDisable() {
        Bukkit.getServer().worlds.forEach { world ->
            world.entities.filterIsInstance<LivingEntity>().forEach(::unrealize)
        }
    }

}