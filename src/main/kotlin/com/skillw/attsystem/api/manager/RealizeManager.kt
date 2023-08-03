package com.skillw.attsystem.api.manager

import com.skillw.pouvoir.api.manager.Manager
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.service.PlatformExecutor

/**
 * Realize manager
 *
 * @constructor Create empty Realize manager
 */
abstract class RealizeManager : Manager {

    /**
     * Realize
     *
     * 实现原版属性
     *
     * @param entity 实体
     */
    abstract fun realize(entity: Entity)

    /** Health regain scheduled */
    abstract var healthRegainScheduled: PlatformExecutor.PlatformTask?
    abstract fun newRealizeTask(entity: LivingEntity): () -> Unit
}
