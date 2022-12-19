package com.skillw.attsystem.api.event

import com.skillw.attsystem.api.fight.FightData
import taboolib.platform.type.BukkitProxyEvent

/**
 * 战斗数据中字符串解析事件
 *
 * 用于自定义战斗数据中的字符串解析 建议拓展PouPAPI来达到此目的(性能更好)
 *
 * @constructor Create empty Fight data handle event
 * @property data 战斗数据
 * @property string 待解析的字符串
 */
class FightDataHandleEvent(
    val data: FightData,
    var string: String,
) : BukkitProxyEvent() {
    override val allowCancelled = false
}
