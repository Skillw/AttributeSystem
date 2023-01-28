package com.skillw.attsystem.api.event

import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import org.bukkit.entity.Entity
import taboolib.platform.type.BukkitProxyEvent

class AttributeUpdateEvent {
    /**
     * 属性更新前 此时上一次的装备属性数据还没有释放
     *
     * @property entity 实体
     * @property data 属性数据集
     */
    class Pre(
        val entity: Entity,
        val data: AttributeDataCompound,
    ) : BukkitProxyEvent() {
        override val allowCancelled = false
    }

    /**
     * 属性更新中 此时新的装备的属性已经加载 但属性映射还没有计算
     *
     * @property entity 实体
     * @property data 属性数据集
     */
    class Process(
        val entity: Entity,
        val data: AttributeDataCompound,
    ) : BukkitProxyEvent() {
        override val allowCancelled = false

    }

    /**
     * 属性更新后 完全新的属性数据 属性映射已计算
     *
     * @property entity 实体
     * @property data 属性数据集
     */
    class Post(
        val entity: Entity,
        val data: AttributeDataCompound,
    ) : BukkitProxyEvent() {
        override val allowCancelled = false

    }
}
