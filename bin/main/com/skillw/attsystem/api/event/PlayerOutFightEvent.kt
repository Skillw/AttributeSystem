package com.skillw.attsystem.api.event

import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

/**
 * 玩家脱离战斗
 *
 * @property player 玩家
 */
class PlayerOutFightEvent(val player: Player) : BukkitProxyEvent()