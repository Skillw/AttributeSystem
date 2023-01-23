package com.skillw.attsystem.api.realizer.component.sub

import com.skillw.attsystem.api.realizer.BaseRealizer
import com.skillw.attsystem.api.realizer.component.ConfigComponent
import com.skillw.attsystem.api.realizer.component.IConfigComponent

/**
 * @className Vanillable
 *
 * @author Glom
 * @date 2023/1/5 16:51 Copyright 2022 user. All rights reserved.
 */
@ConfigComponent
interface Vanillable : IConfigComponent {

    val defaultVanilla: Boolean
    fun isEnableVanilla(): Boolean = (this as? BaseRealizer)?.config?.get("vanilla", false) ?: true


    companion object {
        @JvmStatic
        fun defaultConfig(vanillable: Vanillable, config: MutableMap<String, Any>) {
            config["vanilla"] = vanillable.defaultVanilla
        }
    }
}