package com.skillw.attsystem.api.event

import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import org.bukkit.entity.Entity
import taboolib.platform.type.BukkitProxyEvent

class AttributeUpdateEvent {
    /**
     * 属性更新前
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
     * 属性更新中
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
     * 属性更新后
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
