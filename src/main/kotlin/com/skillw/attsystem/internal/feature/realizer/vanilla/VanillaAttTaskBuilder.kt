package com.skillw.attsystem.internal.feature.realizer.vanilla

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.realizerManager
import com.skillw.attsystem.api.event.VanillaAttributeUpdateEvent
import com.skillw.pouvoir.api.feature.realizer.BaseRealizer
import com.skillw.pouvoir.api.feature.realizer.BaseRealizerManager
import com.skillw.pouvoir.api.feature.realizer.component.*
import com.skillw.pouvoir.util.attribute.BukkitAttribute
import com.skillw.pouvoir.util.attribute.clear
import com.skillw.pouvoir.util.attribute.getAttribute
import org.bukkit.Bukkit
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.util.unsafeLazy
import java.util.*

internal open class VanillaAttTaskBuilder(key: String, val attribute: BukkitAttribute) : BaseRealizer(key), Switchable,
    Vanillable, Valuable, Sync, Awakeable {
    override val file by lazy {
        AttributeSystem.vanilla.file!!
    }
    override val manager: BaseRealizerManager by unsafeLazy {
        realizerManager
    }
    protected val realizeKey = "realizer-vanilla-$key"

    override val defaultEnable: Boolean
        get() = false
    override val defaultValue: String
        get() = "0"
    override val defaultVanilla: Boolean
        get() = true


    private val valuesCache = WeakHashMap<UUID, Double>()

    protected fun changed(uuid: UUID, value: Double): Boolean =
        valuesCache.run {
            return if (get(uuid) != value) {
                put(uuid, value)
                true
            } else false
        }


    override fun newTask(entity: LivingEntity): (() -> Unit)? {
        val uuid = entity.uniqueId
        val value = value(entity)
        if (!changed(uuid, value) || entity.getAttribute(attribute) == null) return null
        val modifier = genModifier(value)
        return {
            entity.getAttribute(attribute)?.run {
                if (!isEnableVanilla()) clear()
                else removeModifier(modifier)
                addModifier(modifier)
                VanillaAttributeUpdateEvent(entity,this@VanillaAttTaskBuilder.attribute, if(isEnableVanilla()) value + 20 else value).call()
            }
        }
    }

    protected fun genModifier(value: Double): AttributeModifier {
        return AttributeModifier(ATTRIBUTE_UUID, "AS-${attribute.name}", value, AttributeModifier.Operation.ADD_NUMBER)
    }

    open fun unrealize(entity: LivingEntity) {
        entity.getAttribute(attribute)?.run {
            removeModifier(genModifier(0.0))
        }
    }

    companion object {
        private val ATTRIBUTE_UUID = UUID.nameUUIDFromBytes("AS_ATTRIBUTE".toByteArray())

        @Awake(LifeCycle.LOAD)
        fun autoRegister() {
            MaxHealthTaskBuilder.register()
            BukkitAttribute.values().filter { it != BukkitAttribute.MAX_HEALTH }.forEach { att ->
                att.toBukkit()?.let { VanillaAttTaskBuilder(att.normalizeName, att).register() }
            }
        }
    }


    override fun whenDisable() {
        Bukkit.getServer().worlds.forEach { world ->
            world.entities.filterIsInstance<LivingEntity>().forEach(::unrealize)
        }
    }

    override fun onDisable() {
        Bukkit.getServer().worlds.forEach { world ->
            world.entities.filterIsInstance<LivingEntity>().forEach(::unrealize)
        }
    }

}