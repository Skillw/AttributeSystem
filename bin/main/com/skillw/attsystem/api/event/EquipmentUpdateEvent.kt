package com.skillw.attsystem.api.event

import com.skillw.attsystem.api.equipment.EquipmentDataCompound
import org.bukkit.entity.Entity
import taboolib.platform.type.BukkitProxyEvent

class EquipmentUpdateEvent {

    /**
     * 装备更新前事件
     *
     * @property entity 实体
     * @property data 装备数据集
     */
    class Pre(
        val entity: Entity,
        val data: EquipmentDataCompound,
    ) : BukkitProxyEvent() {

        override val allowCancelled = false
    }

    /**
     * 装备更新后事件
     *
     * @property entity 实体
     * @property data 装备数据集
     */
    class Post(
        val entity: Entity,
        val data: EquipmentDataCompound,
    ) : BukkitProxyEvent() {

        override val allowCancelled = false
    }

}
