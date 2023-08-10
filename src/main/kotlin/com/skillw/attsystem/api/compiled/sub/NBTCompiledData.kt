package com.skillw.attsystem.api.compiled.sub

import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.attsystem.api.compiled.CompiledData
import org.bukkit.entity.LivingEntity

/**
 * @className NBTCompiledData
 *
 * @author Glom
 * @date 2023/8/2 21:25 Copyright 2023 user. All rights reserved.
 */
class NBTCompiledData(
    attrDataMap: MutableMap<String, Any> = HashMap(),
) : CompiledData() {
    private val condEntries = ArrayList<Entry>()
    private val attrData = AttributeDataCompound.fromMap(attrDataMap)

    /**
     * Entry
     *
     * @constructor Create empty Entry
     * @property pathsToDelete
     */
    class Entry(val pathsToDelete: Collection<String>) : CompiledData() {
        override fun eval(entity: LivingEntity?): AttributeDataCompound {
            TODO("Not yet implemented")
        }
    }

    /**
     * Add
     *
     * @param entry
     */
    fun add(entry: Entry) {
        if (entry.pathsToDelete.isNotEmpty())
            condEntries += entry
        else
            putAllCond(entry)
    }

    override fun eval(entity: LivingEntity?): AttributeDataCompound {
        val clone = attrData.clone()
        if (!condition(entity)) {
            condEntries.flatMap { it.pathsToDelete }.forEach { clone.removeDeep(it) }
        } else {
            condEntries.forEach { entry ->
                if (!entry.condition(entity)) {
                    entry.pathsToDelete.forEach { clone.removeDeep(it) }
                }
            }
        }
        return clone
    }

    override fun serialize(): MutableMap<String, Any> {
        val total = super.serialize()

        return linkedMapOf(
            "NBTCompiledData-${hashCode()}" to linkedMapOf(
                "conditions" to total,
                "attrData" to attrData.serialize(),
                "entries" to condEntries.map {
                    linkedMapOf(
                        "paths" to it.pathsToDelete,
                        "conditions" to it.serialize()
                    )
                },
            )
        )
    }

    override fun hashCode(): Int {
        return super.hashCode() + condEntries.hashCode() + attrData.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NBTCompiledData) return false
        if (!super.equals(other)) return false

        if (attrData != other.attrData) return false
        return condEntries == other.condEntries
    }
}