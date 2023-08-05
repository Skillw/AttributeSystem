package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.conditionManager
import com.skillw.attsystem.api.attribute.compound.AttributeData
import com.skillw.attsystem.api.compiled.sub.ComplexCompiledData
import com.skillw.attsystem.api.compiled.sub.NBTCompiledData
import com.skillw.attsystem.api.compiled.sub.StringsCompiledData
import com.skillw.attsystem.api.manager.CompileManager
import org.bukkit.entity.LivingEntity

object CompileManagerImpl : CompileManager() {
    override val key = "CompileManager"
    override val priority: Int = 7
    override val subPouvoir = AttributeSystem
    override fun compile(
        entity: LivingEntity?,
        nbt: Collection<Any>,
        slot: String?,
    ): (MutableMap<String, Any>) -> NBTCompiledData {
        return { attrDataMap ->
            val total = NBTCompiledData(attrDataMap)
            for (condCompound in nbt) {
                condCompound as? Map<String, Any> ?: continue
                val paths = condCompound["paths"] as? List<String> ?: continue
                val entry = NBTCompiledData.Entry(paths)
                val conditions = condCompound["conditions"] as? List<Map<String, Any>> ?: continue
                conditionManager.matchConditions(conditions, slot).forEach(entry::register)
                total.add(entry)
            }
            total
        }

    }

    override fun compile(
        entity: LivingEntity?,
        string: String,
        slot: String?,
    ): ((AttributeData) -> StringsCompiledData)? {
        val matches = conditionManager.matchConditions(string, slot)
        return if (matches.isNotEmpty())
            { data ->
                StringsCompiledData(data).apply {
                    matches.forEach(this::register)
                }
            }
        else null
    }

    override fun mapping(entity: LivingEntity?): (AttributeData) -> ComplexCompiledData = { data ->
        ComplexCompiledData().apply {
            data.forEach { (attribute, status) ->
                attribute.mapping?.mapping(status, entity)?.also(this::add)
            }
        }
    }


}
