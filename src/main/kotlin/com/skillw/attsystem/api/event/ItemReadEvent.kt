package com.skillw.attsystem.api.event

import com.skillw.attsystem.api.compiled.oper.ComplexCompiledData
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import taboolib.platform.type.BukkitProxyEvent

/**
 * 读取物品事件
 *
 * @constructor Create empty Item read event
 * @property entity 实体
 * @property itemStack 物品
 * @property compiledData 预编译属性数据
 */
class ItemReadEvent(
    val entity: LivingEntity?,
    val itemStack: ItemStack,
    val compiledData: ComplexCompiledData,
    val slot: String?,
) : BukkitProxyEvent() {
    override val allowCancelled = true

}
