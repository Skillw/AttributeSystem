package com.skillw.attsystem.api.event

import com.skillw.pouvoir.api.feature.condition.BaseCondition
import org.bukkit.entity.LivingEntity
import taboolib.platform.type.BukkitProxyEvent
import java.util.regex.Matcher

/**
 * 条件处理事件
 *
 * @property condition 条件
 * @property entity 实体
 * @property matcher 正则匹配器
 * @property text 文本
 * @property pass 是否通过
 */
class ConditionEvent(
    val condition: BaseCondition,
    val entity: LivingEntity?,
    val matcher: Matcher,
    val text: String,
    var pass: Boolean = false,
) : BukkitProxyEvent()
