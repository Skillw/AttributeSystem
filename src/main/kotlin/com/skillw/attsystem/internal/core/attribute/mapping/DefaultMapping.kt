package com.skillw.attsystem.internal.core.attribute.mapping

import com.skillw.attsystem.AttributeSystem.readManager
import com.skillw.attsystem.api.attribute.Mapping
import com.skillw.attsystem.api.compiled.CompiledData
import com.skillw.attsystem.api.read.status.GroupStatus
import com.skillw.attsystem.api.read.status.Status
import com.skillw.attsystem.util.MapUtils.replaceThenCalc
import org.bukkit.entity.LivingEntity

/**
 * @className DefaultMapping
 *
 * @author Glom
 * @date 2023/8/5 15:35 Copyright 2023 user. All rights reserved.
 */
class DefaultMapping(val map: Map<String, Any>) : Mapping() {
    override fun mapping(status: Status<*>, entity: LivingEntity?): CompiledData? {
        attribute ?: return null
        val replacement =
            (status as? GroupStatus<*>)?.readGroup?.run {
                placeholderKeys.associate { "<${it}>" to placeholder(it, attribute!!, status, entity).toString() }
            } ?: return null
        val mapping = map.replaceThenCalc(replacement, entity) as Map<String, Any>
        return readManager.readMap(mapping, entity)
    }
}