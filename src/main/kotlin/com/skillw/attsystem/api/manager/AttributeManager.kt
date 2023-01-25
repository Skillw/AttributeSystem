package com.skillw.attsystem.api.manager

import com.skillw.attsystem.api.attribute.Attribute
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.plugin.SubPouvoir
import com.skillw.pouvoir.api.plugin.map.LowerKeyMap
import java.io.File

/**
 * Attribute manager
 *
 * @constructor Create empty Attribute manager
 */
abstract class AttributeManager : LowerKeyMap<Attribute>(), Manager {

    /** Attributes （按权重排列） */
    abstract val attributes: List<Attribute>
    abstract fun reloadFolder(folder: File)
    abstract fun addDataFolders(folder: File)
    abstract fun addSubPouvoir(subPouvoir: SubPouvoir)
    abstract fun unregister(key: String)
}
