package com.skillw.attsystem.util

import com.skillw.attsystem.AttributeSystem.attributeSystemAPI
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.pouvoir.util.livingEntity
import org.bukkit.entity.LivingEntity
import taboolib.common5.mirrorNow
import java.util.*

object Utils {
    @JvmStatic
    fun UUID.validEntity(): LivingEntity {
        return livingEntity() ?: run {
            attributeSystemAPI.remove(this)
            error("UUID is invalid!")
        }
    }

    @JvmStatic
    fun <T> mirrorIfDebug(id: String, func: () -> T): T {
        return if (ASConfig.debug) {
            mirrorNow(id, func)
        } else {
            func()
        }
    }

}