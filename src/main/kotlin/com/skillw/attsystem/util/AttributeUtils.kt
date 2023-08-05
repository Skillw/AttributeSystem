package com.skillw.attsystem.util

import com.skillw.attsystem.util.nms.NMS
import org.bukkit.attribute.AttributeInstance
import org.bukkit.entity.LivingEntity

object AttributeUtils {

    @JvmStatic
    fun LivingEntity.getAttribute(attribute: BukkitAttribute): AttributeInstance? {
        return NMS.INSTANCE.getAttribute(this, attribute)
    }

    @JvmStatic
    fun AttributeInstance.clear() {
        for (modifier in this.modifiers) {
            this.removeModifier(modifier)
        }
    }
}