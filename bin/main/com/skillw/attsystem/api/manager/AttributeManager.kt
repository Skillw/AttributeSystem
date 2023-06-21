package com.skillw.attsystem.api.manager

import com.skillw.attsystem.api.attribute.Attribute
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.BaseMap
import com.skillw.pouvoir.api.map.LowerKeyMap

/**
 * Attribute manager
 *
 * @constructor Create empty Attribute manager
 */
abstract class AttributeManager : LowerKeyMap<Attribute>(), Manager {

    /** Attributes （按权重排列） */
    abstract val attributes: List<Attribute>
    abstract val attrMap: BaseMap<Attribute, BaseMap<String, String>>
}
