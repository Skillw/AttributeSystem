package com.skillw.attsystem.internal.core.fight

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.debugLang
import com.skillw.attsystem.api.fight.DamageType
import com.skillw.attsystem.api.fight.FightData
import com.skillw.attsystem.api.fight.mechanic.Mechanic
import com.skillw.pouvoir.api.able.Keyable
import com.skillw.pouvoir.api.function.context.IContext
import com.skillw.pouvoir.internal.core.function.context.SimpleContext
import org.bukkit.configuration.serialization.ConfigurationSerializable


/**
 * @className MechanicData
 * @author Glom
 * @date 2022/8/21 10:29
 * Copyright  2022 user. All rights reserved.
 */
class MechanicData(override val key: Mechanic,val type:DamageType,val context: IContext = SimpleContext()) : Keyable<Mechanic>,IContext by context , ConfigurationSerializable{
    private val mechanicKey = key.key
    fun run(fightData: FightData) : Boolean{
        debugLang("fight-info-mechanic", mechanicKey)
        val result = key.run(fightData, context, type)
        if (!fightData.hasResult) return false
        debugLang("fight-info-mechanic-return", result.toString())
        result?.let { fightData[mechanicKey] = it }
        return true
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
                "mechanic" to mechanicKey,
                "context" to context
        )
    }
}