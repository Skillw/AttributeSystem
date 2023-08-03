package com.skillw.attsystem.api.manager

import com.skillw.attsystem.api.attribute.compound.AttributeData
import com.skillw.attsystem.api.compiled.oper.ComplexCompiledData
import com.skillw.attsystem.api.compiled.oper.StringsCompiledData
import com.skillw.attsystem.api.condition.BaseCondition
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.LowerKeyMap
import org.bukkit.entity.LivingEntity

/**
 * Condition manager
 *
 * @constructor Create empty Condition manager
 */
abstract class ConditionManager : LowerKeyMap<BaseCondition>(), Manager {
    /**
     * 构建 NBT预编译属性的构造器
     *
     * @param entity 实体
     * @param nbt NBT
     * @param slot 槽位
     * @return NBT条件的构造器
     */
    abstract fun conditionNBT(
        entity: LivingEntity?,
        nbt: Collection<Any>,
        slot: String? = null,
    ): (MutableMap<String, Any>) -> ComplexCompiledData


    /**
     * 构建 字符串预编译属性的构造器
     *
     * @param entity 实体
     * @param string 字符串
     * @param slot 槽位
     * @return 字符串预编译属性的构造器，若无条件则返回null
     */
    abstract fun condition(
        entity: LivingEntity?,
        string: String,
        slot: String? = null,
    ): ((AttributeData) -> StringsCompiledData)?

}
