package com.skillw.attsystem.api.event

import com.skillw.attsystem.api.fight.FightData
import taboolib.platform.type.BukkitProxyEvent


class FightEvent {
    /**
     * 攻击前事件
     *
     * @property key 战斗组键
     * @property fightData 战斗数据
     */
    class Post(val key: String, val fightData: FightData) : BukkitProxyEvent()

    /**
     * 攻击中事件
     *
     * @property key 战斗组键
     * @property fightData 战斗数据
     */
    class Process(val key: String, val fightData: FightData) : BukkitProxyEvent()

    /**
     * 攻击后事件
     *
     * @property key 战斗组键
     * @property fightData 战斗数据
     */
    class After(val key: String, val fightData: FightData) : BukkitProxyEvent()
}
