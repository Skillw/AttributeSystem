package com.skillw.attsystem.api.realizer

import com.skillw.attsystem.AttributeSystem.realizerManager
import com.skillw.pouvoir.api.plugin.map.component.Registrable
import com.skillw.pouvoir.api.plugin.map.DataMap

/**
 * @className BaseRealizer
 *
 * @author Glom
 * @date 2023/1/5 10:46 Copyright 2022 user. All rights reserved.
 */
abstract class BaseRealizer(final override val key: String) : Registrable<String> {
    val config = DataMap()
    val defaultConfig = LinkedHashMap<String, Any>()
    open val fileName = key.lowercase().replace("-", "_")
    override fun register() {
        realizerManager.register(this)
    }


}