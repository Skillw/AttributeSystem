package com.skillw.attsystem.api

import com.skillw.attsystem.api.attribute.compound.AttributeData
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
     * Read
     *
     * 读取字符串集的属性数据
     *
     * @param strings 待读取属性的字符串集
     * @param entity 实体
     * @param slot 槽位(可为null)
     * @return 属性数据
     */
    fun read(strings: Collection<String>, entity: LivingEntity? = null, slot: String? = null): AttributeData

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
