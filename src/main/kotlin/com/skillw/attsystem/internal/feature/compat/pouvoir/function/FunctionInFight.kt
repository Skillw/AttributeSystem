package com.skillw.attsystem.internal.feature.compat.pouvoir.function


import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.AttrAPI.isFighting
import com.skillw.attsystem.api.fight.FightData
import com.skillw.attsystem.api.operation.NumberOperation
import com.skillw.pouvoir.api.annotation.AutoRegister
import com.skillw.pouvoir.api.function.PouFunction
import com.skillw.pouvoir.api.function.parser.Parser
import org.bukkit.entity.LivingEntity

@AutoRegister
object FunctionInFight : PouFunction<Boolean>("in-fight", namespace = "attsystem") {

    override fun execute(parser: Parser): Boolean {
        with(parser) {
            if (parser.context !is FightData) error("Context must be FightData")
            val entity = parse<LivingEntity>()
            return entity.isFighting()
        }
    }


}