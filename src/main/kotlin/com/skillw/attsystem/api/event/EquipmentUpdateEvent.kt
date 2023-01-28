package com.skillw.attsystem.api.event

import com.skillw.attsystem.api.equipment.EquipmentDataCompound
import org.bukkit.entity.Entity
import taboolib.platform.type.BukkitProxyEvent

class EquipmentUpdateEvent {

    /**
     * 装备更新前事件
     *
     * @property entity 实体
     * @property compound 装备数据集
     */
    class Post(
        val entity: Entity,
        val compound: EquipmentDataCompound,
    ) : BukkitProxyEvent() {

        override val allowCancelled = false
    }


    /**
     * 装备更新中事件
     *
     * @property entity 实体
     * @property compound 装备数据集
     */
    class Process(
        val entity: Entity,
        val compound: EquipmentDataCompound,
    ) : BukkitProxyEvent() {

        override val allowCancelled = false
    }


    /**
     * 装备更新后事件
     *
     * @property entity 实体
     * @property compound 装备数据集
     */
    class After(
        val entity: Entity,
        val compound: EquipmentDataCompound,
    ) : BukkitProxyEvent() {

        override val allowCancelled = false
    }

}
