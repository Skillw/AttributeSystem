package com.skillw.attsystem.api.manager

import com.skillw.pouvoir.api.manager.Manager
import org.bukkit.entity.Entity
import java.util.*

/**
 * Fight status manager
 *
 * @constructor Create empty Fight status manager
 */
abstract class FightStatusManager : Manager {
    /**
     * Is fighting
     *
     * @param uuid
     * @return
     */
    abstract fun isFighting(uuid: UUID): Boolean

    /**
     * Is fighting
     *
     * @param entity
     * @return
     */
    abstract fun isFighting(entity: Entity): Boolean

    /**
     * Into fighting
     *
     * @param entity
     */
    abstract fun intoFighting(entity: Entity)

    /**
     * Into fighting
     *
     * @param uuid
     */
    abstract fun intoFighting(uuid: UUID)

    /**
     * Out fighting
     *
     * @param entity
     */
    abstract fun outFighting(entity: Entity)

    /**
     * Out fighting
     *
     * @param uuid
     */
    abstract fun outFighting(uuid: UUID)

}
