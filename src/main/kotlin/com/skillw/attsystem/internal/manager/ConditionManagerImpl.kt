package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.condition.ConditionData
import com.skillw.attsystem.api.manager.ConditionManager
import com.skillw.attsystem.util.MapUtils.clone
import java.util.*

object ConditionManagerImpl : ConditionManager() {
    override val key = "ConditionManager"
    override val priority: Int = 7
    override val subPouvoir = AttributeSystem

    override fun onEnable() {
        onReload()
    }

    override fun onReload() {
        this.entries.filter { it.value.release }.forEach { this.remove(it.key) }
    }

    override fun matchConditions(text: String, slot: String?): Collection<ConditionData> =
        LinkedList<ConditionData>().apply {
            values.forEach { condition ->
                condition.parameters(text)?.let {
                    this += ConditionData(condition).push(HashMap(it).apply { put("slot", slot) })
                }
            }
        }

    override fun matchConditions(conditions: List<Map<String, Any>>, slot: String?): Collection<ConditionData> =
        LinkedList<ConditionData>().apply {
            conditions.forEach { map ->
                val key = map["key"].toString()
                val condition = get(key) ?: return@forEach
                val args = map.clone() as MutableMap<String, Any>
                slot?.let { args["slot"] = it }
                this += ConditionData(condition).push(args)
            }
        }


}
