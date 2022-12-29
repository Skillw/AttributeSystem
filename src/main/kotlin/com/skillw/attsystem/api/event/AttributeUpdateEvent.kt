package com.skillw.attsystem.api.event

import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import org.bukkit.entity.Entity
import taboolib.platform.type.BukkitProxyEvent

class AttributeUpdateEvent {
    /**
     * 属性更新前 此时上一次的装备属性数据还没有释放
     *
     * @property entity 实体
     * @property compound 属性数据集
     */
    class Post(
        val entity: Entity,
        val compound: AttributeDataCompound,
    ) : BukkitProxyEvent() {
        override val allowCancelled = false

    }

    /**
     * 属性更新中 此时上一次的装备属性数据已经释放了
     *
     * @property entity 实体
     * @property compound 属性数据集
     */
    class Process(
        val entity: Entity,
        val compound: AttributeDataCompound,
    ) : BukkitProxyEvent() {
        override val allowCancelled = false

    }

    /**
     * 属性更新后 完全新的属性数据
     *
     * @property entity 实体
     * @property compound 属性数据集
     */
    class After(
        val entity: Entity,
        val compound: AttributeDataCompound,
    ) : BukkitProxyEvent() {
        override val allowCancelled = false

    }
}
