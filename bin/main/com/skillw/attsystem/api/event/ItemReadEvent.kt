package com.skillw.attsystem.api.event

import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import taboolib.platform.type.BukkitProxyEvent

/**
 * 读取物品事件
 *
 * @constructor Create empty Item read event
 * @property entity 实体
 * @property itemStack 物品
 * @property dataCompound 属性数据集
 */
class ItemReadEvent(
    val entity: LivingEntity?,
    val itemStack: ItemStack,
    val dataCompound: AttributeDataCompound,
    val slot: String?,
) : BukkitProxyEvent() {
    override val allowCancelled = true

}
