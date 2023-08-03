package com.skillw.attsystem.api.compiled.oper

import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.attsystem.api.compiled.CompiledAttrData
import com.skillw.attsystem.util.MapUtils.removeDeep
import org.bukkit.entity.LivingEntity

/**
 * @className NBTCompiledData
 *
 * @author Glom
 * @date 2023/8/2 21:25 Copyright 2023 user. All rights reserved.
 */
class NBTCompiledData(private val attrDataMap: MutableMap<String, Any>, private val paths: Collection<String>) :
    CompiledAttrData() {

    override fun eval(entity: LivingEntity?): AttributeDataCompound {
        val clone: MutableMap<String, Any> = HashMap<String, Any>().apply { putAll(attrDataMap) }
        if (!condition(entity)) {
            paths.forEach { clone.removeDeep(it) }
        }
        return AttributeDataCompound.fromMap(clone).allToRelease()
    }

    override fun serialize(): MutableMap<String, Any> {
        val total = super.serialize()

        return linkedMapOf(
            "NBTCompiledData-${hashCode()}" to linkedMapOf(
                "conditions" to total,
                "attrData" to attrDataMap,
                "pathsToDelete" to paths,
            )
        )
    }

    override fun hashCode(): Int {
        return super.hashCode() + paths.hashCode() + attrDataMap.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NBTCompiledData) return false
        if (!super.equals(other)) return false

        if (attrDataMap != other.attrDataMap) return false
        return paths == other.paths
    }
}