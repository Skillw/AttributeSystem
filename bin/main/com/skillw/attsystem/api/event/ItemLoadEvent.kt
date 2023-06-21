package com.skillw.attsystem.api.event

import org.bukkit.entity.Entity
import org.bukkit.inventory.ItemStack
import taboolib.platform.type.BukkitProxyEvent

/**
 * 加载物品事件
 *
 * @constructor Create empty Item load event
 * @property entity 实体
 * @property itemStack 物品
 */
class ItemLoadEvent(
    val entity: Entity,
    val itemStack: ItemStack,
) : BukkitProxyEvent()