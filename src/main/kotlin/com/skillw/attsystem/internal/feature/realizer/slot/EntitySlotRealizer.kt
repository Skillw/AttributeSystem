package com.skillw.attsystem.internal.feature.realizer.slot

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.equipment.EquipmentDataCompound
import com.skillw.attsystem.api.equipment.EquipmentLoader
import com.skillw.attsystem.api.event.ItemLoadEvent
import com.skillw.attsystem.api.realizer.BaseRealizer
import com.skillw.attsystem.api.realizer.component.sub.Awakeable
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister
import com.skillw.pouvoir.api.plugin.map.LowerMap
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.function.console
import taboolib.module.lang.sendLang
import taboolib.platform.util.isAir
import taboolib.platform.util.isNotAir
import taboolib.type.BukkitEquipment

@AutoRegister
object EntitySlotRealizer : BaseRealizer("entity"), Awakeable {
    private val slots = LowerMap<BukkitEquipment>()
    override val file by lazy {
        AttributeSystem.slot.file!!
    }

    init {
        defaultConfig.putAll(
            linkedMapOf(
                "头盔" to "HEAD",
                "胸甲" to "CHEST",
                "护腿" to "LEGS",
                "靴子" to "FEET",
                "主手" to "HAND",
                "副手" to "OFFHAND"
            )
        )
    }

    override fun onEnable() {
        onReload()
    }

    override fun onReload() {
        slots.clear()
        for (key in config.keys) {
            val slot = config[key].toString()
            val type = kotlin.runCatching { BukkitEquipment.fromString(slot) }.getOrNull()
            type ?: console().sendLang("equipment-type-error", key)
            type ?: continue
            slots.register(key, type)
        }
    }

    @AutoRegister
    object NormalEquipmentLoader : EquipmentLoader<LivingEntity> {

        override val key: String = "default"

        override fun entityType(): Class<*> {
            return LivingEntity::class.java
        }

        override fun loadEquipment(entity: LivingEntity, data: EquipmentDataCompound) {
            for ((key, equipmentType) in slots) {
                //获取装备物品
                val origin = equipmentType.getItem(entity)
                //判空 第一个判空是为了智能推断
                if (origin == null || origin.isAir()) continue
                val event = ItemLoadEvent(entity, origin)
                //触发事件
                event.call()
                if (event.isCancelled) return
                val eventItem = event.itemStack
                // 这个isSimilar判断不出物品的NBT改变 所以会导致悲剧发生
//                if (eventItem.isSimilar(origin)) return
                if (eventItem.isNotAir())
                    data["BASE-EQUIPMENT", key] = eventItem
            }
        }
    }
}