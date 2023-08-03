package com.skillw.attsystem.util

import com.skillw.attsystem.AttributeSystem.attributeSystemAPI
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.attsystem.util.MapUtils.toMutableMap
import com.skillw.pouvoir.util.EntityUtils.livingEntity
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import taboolib.common5.mirrorNow
import taboolib.module.nms.getItemTag
import taboolib.platform.util.isAir
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
    fun ItemStack.isDeepSimilar(other: ItemStack?): Boolean {
        if (isAir() || other.isAir()) {
            return false
        }
        return mirrorIfDebug("benchmark-is-deep-similar") {
            getItemTag().toMutableMap().toString() == other.getItemTag().toMutableMap().toString()
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

    internal fun <T : Any> T.clone(): Any {
        return when (this) {
            is Map<*, *> -> {
                val map = HashMap<String, Any>()
                forEach { (key, value) ->
                    key ?: return@forEach
                    value ?: return@forEach
                    map[key.toString()] = value.clone()
                }
                map
            }

            is List<*> -> {
                val list = LinkedList<Any>()
                mapNotNull { it }.forEach {
                    list.add(it.clone())
                }
                list
            }

            else -> this
        }
    }
}