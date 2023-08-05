package com.skillw.attsystem.api.compiled

import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.attsystem.api.compiled.sub.ComplexCompiledData
import com.skillw.attsystem.internal.feature.realizer.attribute.BaseAttributeEntityRealizer.baseEntity
import com.skillw.attsystem.internal.feature.realizer.attribute.BaseAttributePlayerRealizer.basePlayer
import com.skillw.attsystem.util.Utils.mirrorIfDebug
import com.skillw.pouvoir.api.plugin.map.LowerMap
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

/**
 * @className CompiledAttrDataCompound
 *
 * @author Glom
 * @date 2023/8/3 1:13 Copyright 2023 user. All rights reserved.
 */
class CompiledAttrDataCompound(entity: LivingEntity) : LowerMap<CompiledData>(), Evalable,
    ConfigurationSerializable {

    init {
        if (entity is Player)
            basePlayer()
        else
            baseEntity()
    }

    override fun eval(entity: LivingEntity?): AttributeDataCompound {
        return mirrorIfDebug("compiled-attr-data-eval") {
            val result = AttributeDataCompound(entity)
            val total = ComplexCompiledData()
            forEach { (_, compiledData) ->
                total.add(compiledData)
            }
            val maxLayers = total.layers(1)
            result.combine(total.eval(entity))
            repeat(maxLayers) {
                result.putAll(total.eval(entity))
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