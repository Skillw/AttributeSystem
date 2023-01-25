package com.skillw.attsystem.api.realizer.component.sub


/**
 * @className Realizable
 *
 * @author Glom
 * @date 2023/1/5 16:25 Copyright 2022 user. All rights reserved.
 */
interface Awakeable {
    fun onLoad() {}
    fun onEnable() {}
    fun onReload() {}
    fun onDisable() {}
}