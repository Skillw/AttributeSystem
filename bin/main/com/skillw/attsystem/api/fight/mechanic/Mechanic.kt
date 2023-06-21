package com.skillw.attsystem.api.fight.mechanic

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.event.MechanicRunEvent
import com.skillw.attsystem.api.fight.DamageType
import com.skillw.attsystem.api.fight.FightData
import com.skillw.pouvoir.api.able.Registrable


/**
 * Mechanic
 *
 * @constructor Create empty Mechanic
 * @property key 键
 */
abstract class Mechanic(override val key: String) :
    Registrable<String> {

    /**
     * Exec
     *
     * @param fightData 战斗数据
     * @param context 上下文 （机制在战斗组中的参数）
     * @param damageType 伤害类型
     * @return 返回值
     */
    abstract fun exec(fightData: FightData, context: Map<String, Any>, damageType: DamageType): Any?

    /** 是否在重载时删除 */
    var release = false

    /**
     * Run
     *
     * 运行机制
     *
     * @param fightData 战斗数据
     * @param context 上下文 （机制在战斗组中的参数）
     * @param damageType 伤害类型
     * @return 返回值
     */
    fun run(fightData: FightData, context: Map<String, Any>, damageType: DamageType): Any? {
        val pre = MechanicRunEvent.Post(this, fightData, context, damageType, null)
        if (pre.isCancelled) return null
        val result = exec(fightData, context, damageType)
        val after = MechanicRunEvent.After(this, fightData, context, damageType, result)
        after.call()
        if (after.isCancelled) return null
        return after.result
    }

    final override fun register() {
        AttributeSystem.mechanicManager.register(this)
    }

}
