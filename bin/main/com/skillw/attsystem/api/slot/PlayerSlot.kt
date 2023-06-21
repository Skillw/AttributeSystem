package com.skillw.attsystem.api.slot

import com.skillw.attsystem.AttributeSystem
import com.skillw.pouvoir.api.able.Registrable
import taboolib.common5.Coerce
import taboolib.type.BukkitEquipment

/**
 * Player slot
 *
 * @constructor Create empty Player slot
 * @property key 槽位键
 * @property slot 槽位 ( BukkitEquipment 或 数字)
 * @property requirements 槽位物品要求含有的字符串
 */
class PlayerSlot(override val key: String, val slot: String, val requirements: List<String>) : Registrable<String> {
    /** Bukkit equipment */
    val bukkitEquipment: BukkitEquipment? =
        if (!Coerce.asInteger(slot).isPresent) BukkitEquipment.fromString(slot)
            ?: Coerce.toEnum(
                slot, BukkitEquipment::class.java
            ) else null


    override fun register() {
        AttributeSystem.playerSlotManager.register(key, this)
    }
}
