package com.skillw.attsystem.api.event

import com.skillw.attsystem.api.fight.mechanic.Mechanic
import taboolib.platform.type.BukkitProxyEvent

/**
 * Mechanic register event
 *
 * @constructor Create empty Mechanic register event
 * @property mechanic 机制
 */
class MechanicRegisterEvent(val mechanic: Mechanic) : BukkitProxyEvent()
