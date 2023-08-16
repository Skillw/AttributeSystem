package com.skillw.attsystem.internal.feature.realizer.slot

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.equipment.EquipmentLoader
import com.skillw.pouvoir.api.feature.realizer.BaseRealizer
import com.skillw.pouvoir.api.feature.realizer.BaseRealizerManager
import com.skillw.pouvoir.api.feature.realizer.component.Awakeable
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister
import com.skillw.pouvoir.api.plugin.map.LowerKeyMap
import com.skillw.pouvoir.api.plugin.map.component.Registrable
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.util.unsafeLazy
import taboolib.common5.Coerce
import taboolib.common5.cint
import taboolib.platform.util.hasLore
import taboolib.type.BukkitEquipment

@AutoRegister
object PlayerSlotRealizer : BaseRealizer("player"), Awakeable {
    private val slots = LowerKeyMap<PlayerSlot>()
    override val file by lazy {
        AttributeSystem.slot.file!!
    }
    override val manager: BaseRealizerManager by unsafeLazy {
        AttributeSystem.realizerManager
    }

    override fun onEnable() {
        onReload()
    }

    override fun onReload() {
        slots.clear()
        for (key in config.keys) {
            val value = config[key]
            val slot: String
            var require: String? = null
            if (value is Map<*, *>) {
                val section = value as Map<String, Any>
                slot = section["slot"].toString()
                require = section["require"]?.toString()
            } else {
                slot = value.toString()
            }
            slots.register(PlayerSlot(key, slot.uppercase(), require))
        }

        setOf(
            PlayerSlot("头盔", "HEAD"),
            PlayerSlot("胸甲", "CHEST"),
            PlayerSlot("护腿", "LEGS"),
            PlayerSlot("靴子", "FEET"),
            PlayerSlot("主手", "HAND"),
            PlayerSlot("副手", "OFFHAND")
        ).forEach(slots::register)
    }


    @AutoRegister
    object PlayerEquipmentLoader : EquipmentLoader<Player> {

        override val key: String = "default"

        override val priority: Int = 999

        override fun filter(entity: LivingEntity): Boolean {
            return entity is Player
        }

        override fun loadEquipment(entity: Player): Map<String, ItemStack?> {
            val items = HashMap<String, ItemStack?>()
            for ((slot, playerSlot) in slots) {
                items[slot] = playerSlot.getItem(entity)
            }
            return items
        }
    }

    /**
     * Player slot
     *
     * @constructor Create empty Player slot
     * @property key 槽位键
     * @property slot 槽位 ( BukkitEquipment 或 数字)
     */
    data class PlayerSlot(override val key: String, val slot: String, val require: String? = null) :
        Registrable<String> {
        /** Bukkit equipment */
        val equipment: BukkitEquipment? =
            if (!Coerce.asInteger(slot).isPresent)
                BukkitEquipment.fromString(slot)
                    ?: Coerce.toEnum(slot, BukkitEquipment::class.java)
            else null

        fun getSlot(player: Player): Int {
            return if (slot == "held") player.inventory.heldItemSlot else slot.cint
        }

        fun getItem(player: Player): ItemStack? {
            return (if (equipment == null) {
                player.inventory.getItem(getSlot(player))
            } else {
                equipment.getItem(player)
            }).let {
                if (require != null && it?.hasLore(require) != true) null else it
            }
        }

        override fun register() {
            slots.register(key, this)
        }
    }
}