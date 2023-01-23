package com.skillw.attsystem.api.realizer.component.sub

import com.skillw.attsystem.api.realizer.component.IComponent
import org.bukkit.entity.LivingEntity

/**
 * @className Realizable
 *
 * @author Glom
 * @date 2023/1/5 16:25 Copyright 2022 user. All rights reserved.
 */
interface Realizable : IComponent {
    fun realize(entity: LivingEntity)
    fun unrealize(entity: LivingEntity)
}