package com.skillw.attsystem.api.event

import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import taboolib.platform.type.BukkitProxyEvent

/**
 * Item NBT read event
 *
 * @constructor Create empty Item n b t read event
 * @property entity 实体
 * @property itemStack 物品
 * @property dataCompound 属性数据集
 */
class ItemNBTReadEvent(
    val entity: LivingEntity?,
    val itemStack: ItemStack,
    val dataCompound: AttributeDataCompound,
) : BukkitProxyEvent()
