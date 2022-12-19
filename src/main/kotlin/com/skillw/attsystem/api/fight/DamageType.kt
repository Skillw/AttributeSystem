package com.skillw.attsystem.api.fight

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.fight.message.Message
import com.skillw.pouvoir.api.able.Registrable
import com.skillw.pouvoir.api.map.BaseMap
import com.skillw.pouvoir.util.FileUtils.toMap
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.Player
import taboolib.module.chat.colored

/**
 * Damage type
 *
 * @param messages 各Message对应的文本(内联函数段)
 * @constructor
 * @property key 键
 * @property name 名称
 */
class DamageType(override val key: String, val name: String, messages: Map<String, Any>) : Registrable<String>,
    BaseMap<String, String>(), ConfigurationSerializable {

    init {
        messages.forEach { (key, value) ->
            this[key] = value.toString()
        }
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf("display" to this.map)
    }

    /**
     * Attack message
     *
     * @param player 玩家
     * @param fightData 战斗数据
     * @param first 是否第一次攻击
     * @return 攻击消息
     */
    fun attackMessage(player: Player, fightData: FightData, first: Boolean = false): Message? {
        return AttributeSystem.messageBuilderManager.attack[AttributeSystem.personalManager[player.uniqueId]?.attacking
            ?: "disable"]?.build(this, fightData, first, Message.Type.ATTACK)

    }

    /**
     * Defend message
     *
     * @param player 玩家
     * @param fightData 战斗数据
     * @param first 是否第一次防御
     * @return
     */
    fun defendMessage(player: Player, fightData: FightData, first: Boolean = false): Message? {
        return AttributeSystem.messageBuilderManager.defend[AttributeSystem.personalManager[player.uniqueId]?.defensive
            ?: "disable"]?.build(this, fightData, first, Message.Type.DEFEND)
    }


    companion object {
        @JvmStatic
        fun deserialize(section: org.bukkit.configuration.ConfigurationSection): DamageType {
            val key = section.name
            val name = section.getString("name") ?: key
            val display = HashMap<String, Any>()
            section.getConfigurationSection("display.attack")?.toMap()?.forEach {
                display["attack-${it.key}"] = it.value
            }
            section.getConfigurationSection("display.defend")?.toMap()?.forEach {
                display["defend-${it.key}"] = it.value
            }
            return DamageType(key, name.colored(), display)
        }
    }


    override fun register() {
        AttributeSystem.damageTypeManager.register(this)
    }
}
