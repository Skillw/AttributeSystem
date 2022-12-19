package com.skillw.attsystem.api.fight.message

import com.skillw.attsystem.api.fight.FightData
import org.bukkit.entity.Player

/**
 * MessageType
 *
 * @constructor Create empty MessageType
 */
interface Message {
    val fightData :FightData
    /**
     * Type
     *
     * @constructor Create empty Type
     */
    enum class Type {
        /**
         * Attack 攻击着
         *
         * @constructor Create empty Attack
         */
        ATTACK,

        /**
         * Defend 防御着
         *
         * @constructor Create empty Defend
         */
        DEFEND
    }

    /**
     * Send to
     *
     * @param players 玩家
     */
    fun sendTo(vararg players: Player)

    /**
     * OperationPlus
     *
     * @param message 其他消息
     * @param type 类型
     * @return
     */
    fun plus(message: Message, type: Type): Message

    companion object {
        fun List<Message>.send(type: Type, vararg players: Player) {
            if (this.isEmpty()) return
            var message = this[0]
            for (index in 1 until this.size) {
                message = message.plus(this[index], type)
            }
            message.sendTo(*players)
        }
    }
}
