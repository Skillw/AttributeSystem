package com.skillw.attsystem.api.event

import com.skillw.pouvoir.util.attribute.BukkitAttribute
import org.bukkit.entity.Entity
import taboolib.platform.type.BukkitProxyEvent

/**
 * 原版属性更新后
 *
 * @property entity 实体
 * @property attr 属性
 * @property value 属性值
 */
class VanillaAttributeUpdateEvent (
    val entity: Entity,
    val attr: BukkitAttribute,
    val value: Double,
) : BukkitProxyEvent() {
    override val allowCancelled = false

}
