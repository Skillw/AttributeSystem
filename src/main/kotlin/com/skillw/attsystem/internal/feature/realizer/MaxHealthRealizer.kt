package com.skillw.attsystem.internal.feature.realizer

import com.skillw.attsystem.util.AttributeUtils.clear
import com.skillw.attsystem.util.AttributeUtils.getAttribute
import com.skillw.attsystem.util.BukkitAttribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity

object MaxHealthRealizer : VanillaRealizer("max-health", BukkitAttribute.MAX_HEALTH) {
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
            val maxHealth = value
            if (entity.health > maxHealth) {
                entity.health = maxHealth
            }
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
}