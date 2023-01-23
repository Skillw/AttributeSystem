package com.skillw.attsystem.internal.feature.realizer

import com.skillw.attsystem.api.realizer.BaseRealizer
import com.skillw.attsystem.api.realizer.component.sub.*
import com.skillw.attsystem.util.AttributeUtils.clear
import com.skillw.attsystem.util.AttributeUtils.getAttribute
import com.skillw.attsystem.util.BukkitAttribute
import com.skillw.pouvoir.util.isAlive
import org.bukkit.Bukkit
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

class VanillaRealizer(key: String, val attribute: BukkitAttribute) : BaseRealizer(key), Realizable, Awakeable,
    Switchable, Vanillable, Valuable, Syncable {
    override val fileName: String = "vanilla.yml"
    private val realizeKey = "realizer-vanilla-$key"

    override val defaultEnable: Boolean
        get() = false
    override val defaultValue: String
        get() = "0"
    override val defaultVanilla: Boolean
        get() = true

    override fun realize(entity: LivingEntity) {
        entity.getAttribute(attribute)?.run {
            if (modifiers.isNotEmpty())
                modifiers
                    .firstOrNull { it.name == realizeKey }
                    ?.let {
                        //不检查会报错
                        if (modifiers.contains(it))
                            removeModifier(it)
                    }
            if (!isEnableVanilla()) clear()
            addModifier(
                AttributeModifier(
                    realizeKey,
                    value(entity),
                    AttributeModifier.Operation.ADD_NUMBER
                )
            )
        }
    }

    override fun unrealize(entity: LivingEntity) {
        if (!entity.isAlive()) return
        entity.getAttribute(attribute)?.run {
            if (modifiers.isNotEmpty())
                modifiers.firstOrNull { it.name == realizeKey }?.let {
                    //不检查会报错
                    if (modifiers.contains(it))
                        removeModifier(it)
                }
        }
    }

    companion object {
        private val BukkitAttribute.normalizeName
            get() = name.lowercase().replace("_", "-")

        @Awake(LifeCycle.LOAD)
        fun registerCommon() {
            BukkitAttribute.values().forEach { att ->
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