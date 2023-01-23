package com.skillw.attsystem.api.manager

import com.skillw.attsystem.api.realizer.BaseRealizer
import com.skillw.attsystem.api.realizer.component.sub.Realizable
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.plugin.map.KeyMap

/**
 * Realize manager
 *
 * @constructor Create empty Realize manager
 */
abstract class RealizeManager : Manager, KeyMap<String, BaseRealizer>(), Realizable
