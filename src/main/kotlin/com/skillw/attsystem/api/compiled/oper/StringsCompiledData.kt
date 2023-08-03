package com.skillw.attsystem.api.compiled.oper

import com.skillw.attsystem.api.attribute.compound.AttributeData
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.attsystem.api.compiled.CompiledAttrData
import org.bukkit.entity.LivingEntity

/**
 * @className StringsCompiledData
 *
 * @author Glom
 * @date 2023/8/2 21:25 Copyright 2023 user. All rights reserved.
 */
class StringsCompiledData(val data: AttributeData = AttributeData()) : CompiledAttrData() {

    override fun putAll(other: CompiledAttrData) {
        if (other !is StringsCompiledData) return
        data.combine(other.data)
        super.putAll(other)
    }

    override fun eval(entity: LivingEntity?): AttributeDataCompound {
        return AttributeDataCompound().apply {
            if (condition(entity)) {
                register("STRINGS-ATTRIBUTE", data.clone())
            }
        }.allToRelease()
    }

    override fun serialize(): MutableMap<String, Any> {
        val attrData = data.serialize()
        val total = super.serialize()

        return linkedMapOf(
            "StringsCompiledData-${hashCode()}" to linkedMapOf(
                "conditions" to total,
                "attrData" to attrData
            )
        )
    }

    override fun hashCode(): Int {
        return super.hashCode() + data.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StringsCompiledData) return false
        if (!super.equals(other)) return false

        return data == other.data
    }
}