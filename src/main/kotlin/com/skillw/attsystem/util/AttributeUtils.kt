package com.skillw.attsystem.util

import com.skillw.attsystem.util.nms.NMS
import com.skillw.pouvoir.api.plugin.map.BaseMap
import com.skillw.pouvoir.util.put
import org.bukkit.attribute.AttributeInstance
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import java.util.*

object AttributeUtils {
    private val cache = BaseMap<UUID, BaseMap<BukkitAttribute, AttributeModifier>>()

    @JvmStatic
    fun LivingEntity.realizeAttribute(
        attribute: BukkitAttribute,
        value: Double,
        vanilla: Boolean = false,
    ) {
        val uuid = uniqueId
        with(getAttribute(attribute) ?: return) {
            if (cache[uuid]?.containsKey(attribute) == true) {
                removeModifier(cache[uuid]!![attribute]!!)
            }
            val result = if (!vanilla) {
                clear()
                value - baseValue
            } else value
            val attributeModifier = AttributeModifier(
                uuid,
                attribute.minecraftKey,
                result,
                AttributeModifier.Operation.ADD_NUMBER
            )
            cache.put(uuid, attribute, attributeModifier)
            removeModifier(attributeModifier)
            addModifier(attributeModifier)
        }
    }

    @JvmStatic
    fun LivingEntity.getAttribute(bukkitAttribute: BukkitAttribute): AttributeInstance? {
        return NMS.INSTANCE.getAttribute(this, bukkitAttribute)
    }

    @JvmStatic
    fun AttributeInstance.clear() {
        for (modifier in this.modifiers) {
            this.removeModifier(modifier)
        }
    }
}