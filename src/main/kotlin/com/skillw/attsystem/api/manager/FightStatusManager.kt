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
     * 是否在战斗状态
     *
     * @param uuid
     * @return 是否在战斗状态
     */
    abstract fun isFighting(uuid: UUID): Boolean

    /**
     * 是否在战斗状态
     *
     * @param entity
     * @return 是否在战斗状态
     */
    abstract fun isFighting(entity: Entity): Boolean

    /**
     * 让实体进入战斗状态
     *
     * @param entity
     */
    abstract fun intoFighting(entity: Entity)

    /**
     * 让实体进入战斗状态
     *
     * @param uuid
     */
    abstract fun intoFighting(uuid: UUID)

    /**
     * 让实体脱离战斗状态
     *
     * @param entity
     */
    abstract fun outFighting(entity: Entity)

    /**
     * 让实体脱离战斗状态
     *
     * @param uuid
     */
    abstract fun outFighting(uuid: UUID)

}
