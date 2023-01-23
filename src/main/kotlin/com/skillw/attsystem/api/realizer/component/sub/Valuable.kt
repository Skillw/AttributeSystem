package com.skillw.attsystem.api.realizer.component.sub

import com.skillw.attsystem.api.realizer.BaseRealizer
import com.skillw.attsystem.api.realizer.component.ConfigComponent
import com.skillw.attsystem.api.realizer.component.IConfigComponent
import com.skillw.pouvoir.util.calculateDouble
import org.bukkit.entity.LivingEntity

/**
 * @className Realizable
 *
 * @author Glom
 * @date 2023/1/5 16:25 Copyright 2022 user. All rights reserved.
 */
@ConfigComponent
interface Valuable : IConfigComponent {
    val defaultValue: String

    fun value(entity: LivingEntity? = null): Double {
        return (this as? BaseRealizer)?.config?.get("value", "0.0")!!.calculateDouble(entity)
    }


    companion object {
        @JvmStatic
        fun defaultConfig(valuable: Valuable, config: MutableMap<String, Any>) {
            config["value"] = valuable.defaultValue
        }
    }
}