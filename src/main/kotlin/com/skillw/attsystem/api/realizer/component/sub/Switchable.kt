package com.skillw.attsystem.api.realizer.component.sub

import com.skillw.attsystem.api.realizer.BaseRealizer
import com.skillw.attsystem.api.realizer.component.ConfigComponent
import com.skillw.attsystem.api.realizer.component.IConfigComponent

/**
 * @className Realizable
 *
 * @author Glom
 * @date 2023/1/5 16:25 Copyright 2022 user. All rights reserved.
 */
@ConfigComponent
interface Switchable : IConfigComponent {

    val defaultEnable: Boolean

    fun isEnable(): Boolean = (this as? BaseRealizer)?.config?.get("enable", false) ?: true
    fun isDisable(): Boolean = !isEnable()

    fun whenEnable() {}
    fun whenDisable() {}


    companion object {
        @JvmStatic
        fun defaultConfig(switchable: Switchable, config: MutableMap<String, Any>) {
            config["enable"] = switchable.defaultEnable
        }
    }
}