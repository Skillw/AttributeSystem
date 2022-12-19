package com.skillw.attsystem.internal.core.fight

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.fight.DamageType
import com.skillw.attsystem.api.fight.FightData
import com.skillw.pouvoir.api.able.Keyable
import com.skillw.pouvoir.internal.core.function.context.SimpleContext
import com.skillw.pouvoir.util.FileUtils.toMap
import org.bukkit.configuration.serialization.ConfigurationSerializable
import taboolib.common.platform.function.console
import taboolib.module.lang.sendLang
import java.util.*

/**
 * Mechanic data
 *
 * @constructor Create empty Mechanic data
 * @property key 伤害类型
 * @property enable 是否启用(字符串类型，会被解析)
 */
class MechanicDataCompound private constructor(override val key: DamageType, val enable: String) : Keyable<DamageType>,
    ConfigurationSerializable {
    val process = LinkedList<MechanicData>()

    companion object {

        @JvmStatic
        fun deserialize(section: org.bukkit.configuration.ConfigurationSection): MechanicDataCompound? {
            val damageType = AttributeSystem.damageTypeManager[section.name]
            damageType ?: kotlin.run {
                console().sendLang("invalid-damage-type", section.currentPath.toString())
                return null
            }
            val compound = MechanicDataCompound(damageType, section.getString("enable") ?: "true")
            if (section.contains("mechanics")) {
                val mechanics = section.getList("mechanics") ?: return compound
                for (context in mechanics) {
                    context as? Map<String, Any>? ?: continue
                    val key = context["mechanic"].toString()
                    val machine = AttributeSystem.mechanicManager[key]
                    if (machine == null) {
                        console().sendLang("invalid-mechanic", "${section.currentPath}.$key")
                        continue
                    }
                    compound.process += MechanicData(machine, damageType, SimpleContext(context.toMutableMap()))
                }
                return compound
            }
            for (key in section.getKeys(false)) {
                if (key == "enable") continue
                val machine = AttributeSystem.mechanicManager[key]
                if (machine == null) {
                    console().sendLang("invalid-mechanic", "${section.currentPath}.$key")
                    continue
                }
                compound.process += MechanicData(
                    machine,
                    damageType,
                    SimpleContext(section.getConfigurationSection(key)!!.toMap().toMutableMap())
                )
            }
            return compound
        }
    }

    /**
     * Run
     *
     * @param fightData
     */
    fun run(fightData: FightData): Boolean {
        if (!fightData.handleStr(enable).toBoolean().also {
                fightData["enable"] = it
            }) return false
        for (data in process) if (!data.run(fightData)) break
        return true
    }

    override fun serialize(): MutableMap<String, Any> {
        val map = LinkedHashMap<String, Any>()
        map["enable"] = enable
        map["mechanics"] = process.map { it.serialize() }
        return map
    }


}
