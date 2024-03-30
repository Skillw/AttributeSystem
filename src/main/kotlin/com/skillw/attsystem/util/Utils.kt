package com.skillw.attsystem.util

import com.skillw.attsystem.AttributeSystem.attributeSystemAPI
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.attsystem.util.legacy.mirrorNow
import com.skillw.pouvoir.taboolib.module.nms.MinecraftVersion
import com.skillw.pouvoir.util.livingEntity
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.function.submit
import taboolib.common.platform.service.PlatformExecutor
import java.util.*

object Utils {
    @JvmStatic
    fun UUID.validEntity(): LivingEntity? {

        return livingEntity() ?: run {
            attributeSystemAPI.remove(this)
            null
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


    @JvmStatic
    fun adaptive(
        now: Boolean = false,
        delay: Long = 0,
        period: Long = 0,
        executor: PlatformExecutor.PlatformTask.() -> Unit,
    ): PlatformExecutor.PlatformTask {
        MinecraftVersion
        return submit(now, true, delay, period, executor)
    }


}