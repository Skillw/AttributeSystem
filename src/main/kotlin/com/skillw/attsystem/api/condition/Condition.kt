package com.skillw.attsystem.api.condition

import org.bukkit.entity.LivingEntity
import java.util.regex.Matcher

/**
 * @className Condition
 *
 * @author Glom
 * @date 2022/7/18 23:55 Copyright 2022 user. All rights reserved.
 */
interface Condition {
    /**
     * Lore Condition
     *
     * @param slot 槽位 (可为null)
     * @param entity 实体 (可为null)
     * @param matcher 匹配器
     * @param text 文本
     * @return 是否满足条件
     */
    fun condition(
        slot: String?,
        entity: LivingEntity?,
        matcher: Matcher,
        text: String,
    ): Boolean

    /**
     * NBT Condition
     *
     * @param slot 槽位 (可为null)
     * @param entity 实体 (可为null)
     * @param map NBT
     * @return 是否满足条件
     */
    fun conditionNBT(
        slot: String?,
        entity: LivingEntity?,
        map: Map<String, Any>,
    ): Boolean
}