package com.skillw.attsystem.api.manager

import com.skillw.attsystem.api.fight.mechanic.Mechanic
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.KeyMap

/**
 * Mechanic manager
 *
 * @constructor Create empty Mechanic manager
 */
abstract class MechanicManager : KeyMap<String, Mechanic>(), Manager
