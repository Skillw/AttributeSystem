package com.skillw.attsystem.api.event

import org.bukkit.entity.LivingEntity
import taboolib.platform.type.BukkitProxyEvent

/**
 * 回血事件
 *
 * @constructor Create empty Health regain event
 * @property entity 实体
 * @property regain 回复量
 */
class HealthRegainEvent(val entity: LivingEntity, var regain: Double) : BukkitProxyEvent()