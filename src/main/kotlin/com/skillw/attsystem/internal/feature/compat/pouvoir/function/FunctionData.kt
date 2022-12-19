package com.skillw.attsystem.internal.feature.compat.pouvoir.function


import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.fight.FightData
import com.skillw.attsystem.api.operation.NumberOperation
import com.skillw.pouvoir.api.annotation.AutoRegister
import com.skillw.pouvoir.api.function.PouFunction
import com.skillw.pouvoir.api.function.parser.Parser

@AutoRegister
internal object FunctionData : PouFunction<Any?>("data", namespace = "attsystem") {

    override fun execute(parser: Parser): Any? {
        with(parser) {
            if (parser.context !is FightData) error("Context must be FightData")
            val data = parser.context as FightData
            return when (val main = parseString()) {
                "damage" -> processDamageSource(parser)
                "hasResult" ->
                    if (except("=", "to")) {
                        data.hasResult = parseBoolean()
                        data.hasResult
                    } else {
                        data.hasResult
                    }

                "calMessage" ->
                    if (except("=", "to")) {
                        data.calMessage = parseBoolean()
                        data.calMessage
                    } else {
                        data.calMessage
                    }

                "put" -> {
                    val key = parseString()
                    except("to")
                    val value = parseAny() ?: error("Value must be not null")
                    data.put(key, value)
                }

                "get" -> {
                    val key = parseString()
                    data[key]
                }

                "remove" -> {
                    val key = parseString()
                    data.remove(key)
                }

                "has" -> {
                    val key = parseString()
                    data.containsKey(key)
                }

                "size" -> {
                    data.size
                }

                "clear" -> {
                    data.clear()
                }

                "isEmpty" -> {
                    data.isEmpty()
                }

                "keys" -> {
                    data.keys
                }

                else -> {
                    error("Invalid Data token $main")
                }
            }
        }
    }

    private fun processDamageSource(parser: Parser) {
        val data = parser.context as FightData
        with(parser) {
            when (val token = parseString()) {
                "put" -> {
                    val key = parseString()
                    except("with")
                    val operation = AttributeSystem.operationManager[parseString()] as? NumberOperation?
                        ?: error("parse Number Operation Error")
                    except("to")
                    val value = parseDouble()
                    data.damageSources[key] = operation.element(value)
                }

                "remove" -> data.damageSources.remove(parseString())
                "has" -> data.damageSources.containsKey(parseString())
                "get" -> data.damageSources[parseString()]
                "size" -> data.damageSources.size
                "clear" -> data.damageSources.clear()
                "isEmpty" -> data.damageSources.isEmpty()
                "keys" -> data.damageSources.keys
                else -> error("Invalid Data Damage Action Type $token")
            }
        }
    }
}