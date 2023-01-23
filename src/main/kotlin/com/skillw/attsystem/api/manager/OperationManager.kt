package com.skillw.attsystem.api.manager

import com.skillw.attsystem.api.operation.Operation
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.plugin.map.LowerKeyMap

/**
 * NumberOperation manager
 *
 * @constructor Create empty NumberOperation manager
 */
abstract class OperationManager : LowerKeyMap<Operation<*>>(), Manager
