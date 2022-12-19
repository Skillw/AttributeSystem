package com.skillw.attsystem.api.manager

import com.skillw.attsystem.internal.core.fight.FightGroup
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.KeyMap

/**
 * Fight group manager
 *
 * @constructor Create empty Fight group manager
 */
abstract class FightGroupManager : KeyMap<String, FightGroup>(), Manager
