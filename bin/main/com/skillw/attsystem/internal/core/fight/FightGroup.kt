package com.skillw.attsystem.internal.core.fight

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.debugLang
import com.skillw.attsystem.api.fight.DamageType
import com.skillw.attsystem.api.fight.FightData
import com.skillw.attsystem.internal.core.operation.num.OperationPlus
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.pouvoir.api.able.Registrable
import com.skillw.pouvoir.api.map.LinkedKeyMap
import com.skillw.pouvoir.util.MessageUtils.info
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.Player

/**
 * Fight group
 *
 * @constructor Create empty Fight group
 * @property key 战斗组键
 */
class FightGroup constructor(
    override val key: String,
) : Registrable<String>,
    ConfigurationSerializable, LinkedKeyMap<DamageType, MechanicDataCompound>() {

    /** Damage types */
    val damageTypes = this.list

    /**
     * Run
     *
     * @param originData 原战斗数据
     * @return
     */
    internal fun run(originData: FightData): Double {
        debugLang("fight-info")
        debugLang("fight-info-key", key)
        debugLang("fight-info-attacker", originData["attacker-name"].toString())
        debugLang("fight-info-defender", originData["defender-name"].toString())

        for (index in damageTypes.indices) {
            val type = damageTypes[index]
            debugLang("fight-info-damage-type", type.name)
            val fightData = FightData(originData)
            if (!this[type]!!.run(fightData)) continue
            val result = fightData.calResult()
            fightData["result"] = result
            debugLang("fight-info-usable-vars")
            ASConfig.debug {
                fightData.forEach {
                    if (it.key.startsWith("type::")) return@forEach
                    if (it.value::class.java.simpleName.contains("Function", true)) return@forEach
                    info("      type::${type.key}-${it.key} : ${it.value}")
                    originData["type::${type.key}-${it.key}"] = it.value
                }
            }
            debugLang("fight-info-message")
            val attacker = originData.attacker
            val defender = originData.defender
            if (attacker is Player && fightData.calMessage) {
                type.attackMessage(attacker, fightData, originData.messageData.attackMessages.isEmpty())
                    ?.also { originData.messageData.attackMessages.add(it) }
            }
            if (defender is Player && fightData.calMessage) {
                type.defendMessage(defender, fightData, originData.messageData.defendMessages.isEmpty())
                    ?.also { originData.messageData.defendMessages.add(it) }

            }
            originData.damageSources[type.key] = OperationPlus.element(result)
        }
        val result = originData.calResult()
        debugLang("fight-info-result", result.toString())
        return result
    }

    override fun serialize(): MutableMap<String, Any> {
        val map = LinkedHashMap<String, Any>()
        for (damageType in damageTypes) {
            map[damageType.key] = this[damageType]?.serialize() ?: continue
        }
        return map
    }

    companion object {
        @JvmStatic
        fun deserialize(section: org.bukkit.configuration.ConfigurationSection): FightGroup? {
            val key = section.name
            val fightGroup = FightGroup(key)
            for (damageTypeKey in section.getKeys(false)) {
                val damageType = AttributeSystem.damageTypeManager[damageTypeKey] ?: continue
                fightGroup[damageType] =
                    MechanicDataCompound.deserialize(section.getConfigurationSection(damageTypeKey)!!) ?: continue
            }
            return fightGroup
        }
    }

    override fun register() {
        AttributeSystem.fightGroupManager.register(this)
    }
}
