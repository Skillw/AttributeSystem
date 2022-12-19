package com.skillw.attsystem.api.manager

import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.LowerMap
import taboolib.type.BukkitEquipment

/**
 * Entity slot manager
 *
 * @constructor Create empty Entity slot manager
 */
abstract class EntitySlotManager : LowerMap<BukkitEquipment>(), Manager
