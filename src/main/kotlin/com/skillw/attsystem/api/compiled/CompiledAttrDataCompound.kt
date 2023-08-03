package com.skillw.attsystem.api.compiled

import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.attsystem.util.Utils.mirrorIfDebug
import com.skillw.pouvoir.api.map.LowerMap
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.LivingEntity

/**
 * @className CompiledAttrDataCompound
 *
 * @author Glom
 * @date 2023/8/3 1:13 Copyright 2023 user. All rights reserved.
 */
class CompiledAttrDataCompound : LowerMap<CompiledAttrData>(), Evalable, ConfigurationSerializable {


    override fun eval(entity: LivingEntity?): AttributeDataCompound {
        return mirrorIfDebug("compiled-attr-data-eval") {
            val result = AttributeDataCompound(entity)
            forEach { (_, compiledData) ->
                result.combine(compiledData.eval(entity))
            }
            result.allToRelease()
        }
    }

    override fun serialize(): MutableMap<String, Any> {

        val children = LinkedHashMap<String, Any>()
        this.entries.forEach { (key, data) ->
            children[key] = data.serialize()
        }
        return linkedMapOf(
            "CompiledAttrDataCompound" to children
        )
    }

}