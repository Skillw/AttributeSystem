package com.skillw.attsystem.api.manager

import com.skillw.attsystem.api.fight.DamageType
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.KeyMap

/**
 * Damage type manager
 *
 * @constructor Create empty Damage type manager
 */
abstract class DamageTypeManager : KeyMap<String, DamageType>(), Manager
