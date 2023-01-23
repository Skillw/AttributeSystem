package com.skillw.attsystem.api.manager

import com.skillw.attsystem.api.condition.BaseCondition
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.plugin.map.LowerKeyMap
import org.bukkit.entity.LivingEntity

/**
 * Condition manager
 *
 * @constructor Create empty Condition manager
 */
abstract class ConditionManager : LowerKeyMap<BaseCondition>(), Manager {
    /**
     * Condition NBT
     *
     * NBT条件
     *
     * @param slot 槽位
     * @param entity 实体
     * @param map NBT
     * @return 要去除的NBT属性的键
     */
    abstract fun conditionNBT(
        slot: String? = null,
        entity: LivingEntity?,
        map: Map<String, Any>,
    ): Set<String>

    /**
     * Condition line
     *
     * 单行条件
     *
     * @param slot 槽位
     * @param entity 实体
     * @param str 字符串
     * @return 是否通过
     */
    abstract fun conditionLine(slot: String? = null, entity: LivingEntity?, str: String): Boolean

    /**
     * Condition strings
     *
     * 多行条件
     *
     * @param slot 槽位
     * @param entity 实体
     * @param strings 字符串集
     * @return 是否通过
     */
    abstract fun conditionStrings(
        slot: String? = null,
        entity: LivingEntity?,
        strings: Collection<String>,
    ): Boolean

    /**
     * Line conditions
     *
     * 单行条件
     *
     * @param slot 槽位
     * @param requirements 多个单行条件
     * @param entity 实体
     * @return 是否通过
     */
    abstract fun lineConditions(slot: String? = null, requirements: String, entity: LivingEntity?): Boolean
}
