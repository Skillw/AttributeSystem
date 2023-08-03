package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.attribute.compound.AttributeData
import com.skillw.attsystem.api.compiled.ConditionData
import com.skillw.attsystem.api.compiled.oper.ComplexCompiledData
import com.skillw.attsystem.api.compiled.oper.NBTCompiledData
import com.skillw.attsystem.api.compiled.oper.StringsCompiledData
import com.skillw.attsystem.api.manager.ConditionManager
import com.skillw.attsystem.util.Utils.clone
import org.bukkit.entity.LivingEntity
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

    private fun matches(text: String, slot: String?): Collection<ConditionData> {
        val datas = LinkedList<ConditionData>()
        for ((_, condition) in this) {
            condition.parameters(text)?.let {
                datas += ConditionData(condition).push(HashMap(it).apply { put("slot", slot) })
            }
        }
        return datas
    }

    override fun conditionNBT(
        entity: LivingEntity?,
        nbt: Collection<Any>,
        slot: String?,
    ): (MutableMap<String, Any>) -> ComplexCompiledData {
        return { attrDataMap ->
            val total = ComplexCompiledData()
            for (condCompound in nbt) {
                condCompound as? Map<String, Any> ?: continue
                val paths = condCompound["paths"] as? List<String> ?: continue
                val nbtConOperator = NBTCompiledData(attrDataMap.clone() as MutableMap<String, Any>, paths)
                val conditions = condCompound["conditions"] as? List<Map<String, Any>> ?: continue
                inner@ for (map in conditions) {
                    val key = map["key"].toString()
                    val condition = get(key) ?: continue
                    val args = HashMap(map)
                    args["slot"] = slot
                    val data = ConditionData(condition).push(args)
                    nbtConOperator.register(data)
                }
                total.add(nbtConOperator)
            }
            total
        }

    }

    override fun condition(
        entity: LivingEntity?,
        string: String,
        slot: String?,
    ): ((AttributeData) -> StringsCompiledData)? {
        val matches = matches(string, slot)
        return if (matches.isNotEmpty()) { data ->
            val total = StringsCompiledData(data)
            matches.forEach { condData ->
                total.register(condData)
            }
            total
        } else null
    }


}
