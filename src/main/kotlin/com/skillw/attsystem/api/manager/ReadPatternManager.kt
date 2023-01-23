package com.skillw.attsystem.api.manager

import com.skillw.attsystem.api.read.ReadPattern
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.plugin.map.LowerKeyMap

/**
 * Read pattern manager
 *
 * @constructor Create empty Read pattern manager
 */
abstract class ReadPatternManager : LowerKeyMap<ReadPattern<*>>(), Manager {}
