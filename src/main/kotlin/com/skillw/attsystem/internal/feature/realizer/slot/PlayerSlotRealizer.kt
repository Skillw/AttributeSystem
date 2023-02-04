package com.skillw.attsystem.internal.feature.realizer.slot

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.equipment.EquipmentDataCompound
import com.skillw.attsystem.api.equipment.EquipmentLoader
import com.skillw.attsystem.api.event.ItemLoadEvent
import com.skillw.attsystem.api.realizer.BaseRealizer
import com.skillw.attsystem.api.realizer.component.sub.Awakeable
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister
import com.skillw.pouvoir.api.plugin.map.LowerKeyMap
import com.skillw.pouvoir.api.plugin.map.component.Registrable
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common5.Coerce
import taboolib.common5.cint
import taboolib.module.nms.getItemTag
import taboolib.platform.util.isAir
import taboolib.platform.util.isNotAir
import taboolib.type.BukkitEquipment

@AutoRegister
object PlayerSlotRealizer : BaseRealizer("player"), Awakeable {
    private val slots = LowerKeyMap<PlayerSlot>()
    override val file by lazy {
        AttributeSystem.slot.file!!
    }

    override fun onEnable() {
        onReload()
    }

    override fun onReload() {
        slots.clear()
        for (key in config.keys) {
            val slot: String
            val value = config[key]
            if (value is Map<*, *>) {
                val section = value as Map<String, Any>
                slot = section["slot"].toString()
            } else {
                slot = value.toString()
            }
            slots.register(PlayerSlot(key, slot.uppercase()))
        }
    }


    @AutoRegister
    object PlayerEquipmentLoader : EquipmentLoader<Player> {

        override val key: String = "default"

        override val priority: Int = 999

        override fun filter(entity: LivingEntity): Boolean {
            return entity is Player
        }

        override fun loadEquipment(entity: Player, data: EquipmentDataCompound) {
            for (playerSlot in slots.values) {
                val origin: ItemStack? = playerSlot.getItem(entity)
                if (origin == null || origin.isAir() || !origin.hasItemMeta()) continue
                val event = ItemLoadEvent(entity, origin)
                event.call()
                if (event.isCancelled) return
                val eventItem = event.itemStack
                if (eventItem.isNotAir() && !eventItem.getItemTag().containsKey("IGNORE_ATTRIBUTE"))
                    data["BASE-EQUIPMENT", playerSlot.key] = eventItem
            }
        }
    }

    /**
     * Player slot
     *
     * @constructor Create empty Player slot
     * @property key 槽位键
     * @property slotStr 槽位 ( BukkitEquipment 或 数字)
     * @property requirements 槽位物品要求含有的字符串
     */
    data class PlayerSlot(override val key: String, val slotStr: String) :
        Registrable<String> {
        /** Bukkit equipment */
        val bukkitEquipment: BukkitEquipment? =
            if (!Coerce.asInteger(slotStr).isPresent)
                BukkitEquipment.fromString(slotStr)
                    ?: Coerce.toEnum(slotStr, BukkitEquipment::class.java)
            else null

        fun getSlot(player: Player): Int {
            return if (slotStr == "held") player.inventory.heldItemSlot else slotStr.cint
        }

        fun getItem(player: Player): ItemStack? {
            val equipmentType = bukkitEquipment
            return if (equipmentType != null) {
                equipmentType.getItem(player)
            } else {
                player.inventory.getItem(getSlot(player))
            }
        }

        override fun register() {
            slots.register(key, this)
        }
    }
}