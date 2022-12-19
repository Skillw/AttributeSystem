package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.event.ItemCooldownEvent
import com.skillw.attsystem.api.manager.CooldownManager
import com.skillw.pouvoir.util.MapUtils.put
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.lang.Double.min
import java.util.*
import kotlin.math.roundToInt

object CooldownManagerImpl : CooldownManager() {
    override val key = "CooldownManager"
    override val priority: Int = 13
    override val subPouvoir = AttributeSystem

    override fun remove(key: UUID, material: Material) {
        get(key)?.remove(material)
    }

    override fun remove(player: Player, material: Material) {
        remove(player.uniqueId, material)
    }

    override fun pull(key: UUID, material: Material): Double {
        if (!this.containsKey(key) || !this[key]!!.containsKey(material)) {
            return 1.0
        }
        val total = this[key]?.get(material)?.total ?: return 1.0
        val start = this[key]?.get(material)?.start ?: return 1.0
        val now = System.currentTimeMillis()
        val value = min((now - start) / total, 1.0)
        remove(key, material)
        return value
    }

    override fun pull(player: Player, material: Material): Double {
        return pull(player.uniqueId, material)
    }

    override fun getItemCoolDown(player: Player, material: Material): Int {
        return player.getCooldown(material)
    }

    override fun isItemCoolDown(player: Player, slot: Int): Boolean {
        return getItemCoolDown(player, slot) > 0
    }

    override fun isItemCoolDown(player: Player, itemStack: ItemStack): Boolean {
        return getItemCoolDown(player, itemStack) > 0
    }

    override fun setItemCoolDown(player: Player, material: Material, attackSpeed: Double) {
        if (attackSpeed <= 0.0) {
            return
        }
        if (ASConfig.disableCooldownTypes.contains(material)) return
        val originCoolDown: Double = 1 / attackSpeed
        put(player.uniqueId, material, CooldownTime(originCoolDown))
        if (ASConfig.enableCooldown)
            player.setCooldown(material, (originCoolDown * 20).roundToInt())
    }

    override fun setItemCoolDown(player: Player, slot: Int, attackSpeed: Double) {
        if (attackSpeed <= 0.0) {
            return
        }
        val item = player.inventory.getItem(slot)
        val event = ItemCooldownEvent(player, item!!, attackSpeed)
        event.call()
        if (!event.isCancelled)
            setItemCoolDown(player, item.type, event.cooldown)
    }

    override fun setItemCoolDown(player: Player, itemStack: ItemStack, attackSpeed: Double) {
        if (attackSpeed <= 0.0) {
            return
        }
        setItemCoolDown(player, itemStack.type, attackSpeed)
    }

    override fun getItemCoolDown(player: Player, itemStack: ItemStack): Int {
        val material = itemStack.type
        return player.getCooldown(material)
    }


    override fun getItemCoolDown(player: Player, slot: Int): Int {
        val item = player.inventory.getItem(slot)
        val material = item!!.type
        return player.getCooldown(material)
    }
}
