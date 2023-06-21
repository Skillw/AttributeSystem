package com.skillw.attsystem.api

import com.skillw.attsystem.api.attribute.compound.AttributeData
import com.skillw.attsystem.api.fight.FightData
import com.skillw.pouvoir.api.manager.Manager
import org.bukkit.entity.LivingEntity
import java.util.*
import java.util.function.Consumer

/**
 * Attribute system a p i
 *
 * @constructor Create empty Attribute system a p i
 */
interface AttributeSystemAPI : Manager {

    /** 是否跳过下次战斗组计算 */
    fun skipNextDamageCal()

    /**
     * Entity attack cal
     *
     * 实体间战斗组计算 (不计算战斗消息)
     *
     * @param key 战斗组键
     * @param attacker 攻击者
     * @param defender 防御者
     * @param consumer 战斗数据处理
     * @return 最终伤害结果
     */
    @Deprecated("请使用 [runFight] 方法")
    fun entityAttackCal(
        key: String,
        attacker: LivingEntity?,
        defender: LivingEntity,
        consumer: Consumer<FightData>,
    ): Double


    /**
     * Entity attack cal
     *
     * @param key
     * @param data
     * @return
     */
    fun runFight(key: String, data: FightData, message: Boolean = true): Double

    /**
     * Player attack cal
     *
     * 玩家间战斗组计算 (计算战斗消息)
     *
     * @param key 战斗组键
     * @param attacker 攻击者
     * @param defender 防御者
     * @param consumer 战斗数据处理
     * @return 最终伤害结果
     */
    @Deprecated("请使用 [runFight] 方法")
    fun playerAttackCal(
        key: String, attacker: LivingEntity?, defender: LivingEntity, consumer: Consumer<FightData>,
    ): Double

    /**
     * Read
     *
     * 读取字符串集的属性数据
     *
     * @param strings 待读取属性的字符串集
     * @param entity 实体
     * @param slot 槽位(可为null)
     * @return 属性数据
     */
    fun read(strings: Collection<String>, entity: LivingEntity? = null, slot: String? = null): AttributeData

    /**
     * EntityUpdate
     *
     * 更新实体(装备 属性 原版属性实现)
     *
     * 建议异步调用
     *
     * @param entity 实体
     */
    fun update(entity: LivingEntity)

    /**
     * Remove
     *
     * 删除一个实体的所有AS数据
     *
     * @param entity 实体
     */
    fun remove(entity: LivingEntity)

    /**
     * Remove
     *
     * 删除一个实体的所有AS数据
     *
     * @param uuid 实体UUID
     */
    fun remove(uuid: UUID)
}
