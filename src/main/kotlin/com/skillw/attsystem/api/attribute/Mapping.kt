package com.skillw.attsystem.api.attribute

import com.skillw.attsystem.api.compiled.CompiledData
import com.skillw.attsystem.api.read.status.Status
import org.bukkit.entity.LivingEntity

/**
 * @className Mapping
 *
 * @author Glom
 * @date 2023/8/5 14:57 Copyright 2023 user. All rights reserved.
 */
abstract class Mapping {
    var attribute: Attribute? = null
    abstract fun mapping(status: Status<*>, entity: LivingEntity?): CompiledData?
}