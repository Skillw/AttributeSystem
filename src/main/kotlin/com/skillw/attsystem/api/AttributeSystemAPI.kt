package com.skillw.attsystem.api

import com.skillw.pouvoir.api.manager.Manager
import org.bukkit.entity.LivingEntity
import java.util.*

/**
 * Attribute system a p i
 *
 * @constructor Create empty Attribute system a p i
 */
interface AttributeSystemAPI : Manager {

    /**
     * EntityUpdate
     *
     * 更新实体(装备 属性 原版属性实现)
     *
     * 建议异步调用
     *
     * @param entity 实体
     */
    fun update(entity: LivingEntity)

    /**
     * Remove
     *
     * 删除一个实体的所有AS数据
     *
     * @param entity 实体
     */
    fun remove(entity: LivingEntity)

    /**
     * Remove
     *
     * 删除一个实体的所有AS数据
     *
     * @param uuid 实体UUID
     */
    fun remove(uuid: UUID)
}
