package com.skillw.attsystem.api.manager

import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.BaseMap
import org.bukkit.entity.Entity
import java.util.*

/**
 * Formula manager
 *
 * @constructor Create empty Formula manager
 */
abstract class FormulaManager : BaseMap<String, String>(), Manager {
    /**
     * Calculate
     *
     * @param uuid 实体UUID
     * @param key 公式键
     * @param replacement 公式替换
     * @return 计算结果
     */
    abstract fun calculate(uuid: UUID, key: String, replacement: Map<String, String> = emptyMap()): Double

    /**
     * Calculate
     *
     * @param entity 实体
     * @param key 公式键
     * @param replacement 公式替换
     * @return 计算结果
     */
    abstract fun calculate(entity: Entity, key: String, replacement: Map<String, String> = emptyMap()): Double

    /**
     * Get
     *
     * @param uuid 实体UUID
     * @param key 公式键
     * @return 公式值
     */
    abstract operator fun get(uuid: UUID, key: String): Double

    /**
     * Get
     *
     * @param entity 实体
     * @param key 公式键
     * @return 公式值
     */
    abstract operator fun get(entity: Entity, key: String): Double
}
