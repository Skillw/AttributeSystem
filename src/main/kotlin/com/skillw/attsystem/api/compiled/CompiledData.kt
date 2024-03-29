package com.skillw.attsystem.api.compiled

import com.skillw.pouvoir.api.feature.condition.Condition
import com.skillw.pouvoir.api.feature.condition.ConditionData
import com.skillw.pouvoir.api.plugin.map.KeyMap
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.LivingEntity

/**
 * @className CompiledData
 *
 * @author Glom
 * @date 2023/8/2 18:51 Copyright 2023 user. All rights reserved.
 */
abstract class CompiledData : KeyMap<Condition, ConditionData>(), Evalable, ConfigurationSerializable {


    fun putAllCond(other: KeyMap<Condition, ConditionData>) {
        other.forEach { (condition, conditionData) ->
            computeIfAbsent(condition) { ConditionData(condition) }.addAll(conditionData)
        }
    }

    open fun putAll(other: CompiledData) {
        putAllCond(other)
    }

    open fun condition(entity: LivingEntity?): Boolean {
        return values.all { data ->
            data.condition(entity)
        }
    }

    override fun serialize(): MutableMap<String, Any> {
        val total = LinkedHashMap<String, Any>()
        values.forEach {
            total.putAll(it.serialize())
        }
        return total
    }


}