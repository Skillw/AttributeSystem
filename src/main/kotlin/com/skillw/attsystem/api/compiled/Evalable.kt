package com.skillw.attsystem.api.compiled

import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import org.bukkit.entity.LivingEntity

/**
 * @className Evalable
 *
 * @author Glom
 * @date 2023/8/3 1:31 Copyright 2023 user. All rights reserved.
 */
fun interface Evalable {
    fun eval(entity: LivingEntity?): AttributeDataCompound
}