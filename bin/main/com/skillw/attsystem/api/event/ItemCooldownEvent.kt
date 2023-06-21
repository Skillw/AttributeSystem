package com.skillw.attsystem.api.event

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.platform.type.BukkitProxyEvent

/**
 * 物品冷却事件
 *
 * @constructor Create empty Item cooldown event
 * @property player 玩家
 * @property itemStack 物品
 * @property cooldown 物品冷却时间 （秒）
 */
class ItemCooldownEvent(
    val player: Player,
    val itemStack: ItemStack,
    var cooldown: Double,
) : BukkitProxyEvent()