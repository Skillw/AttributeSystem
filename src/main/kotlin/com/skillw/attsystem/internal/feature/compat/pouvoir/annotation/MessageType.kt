package com.skillw.attsystem.internal.feature.compat.pouvoir.annotation

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.fight.DamageType
import com.skillw.attsystem.api.fight.FightData
import com.skillw.attsystem.api.fight.message.Message
import com.skillw.attsystem.api.fight.message.MessageBuilder
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.pouvoir.Pouvoir
import com.skillw.pouvoir.api.annotation.AutoRegister
import com.skillw.pouvoir.api.map.BaseMap
import com.skillw.pouvoir.api.script.annotation.ScriptAnnotation
import com.skillw.pouvoir.api.script.annotation.ScriptAnnotationData
import com.skillw.pouvoir.util.StringUtils.toArgs
import org.bukkit.entity.Player
import taboolib.common.platform.function.console
import taboolib.module.lang.sendLang
import javax.script.ScriptContext.ENGINE_SCOPE

/**
 * MessageType
 *
 * @constructor MessageType Key String
 */
@AutoRegister
object MessageType : ScriptAnnotation("MessageType", fileAnnotation = true) {
    private val messageCache = BaseMap<String, Message>()
    override fun handle(data: ScriptAnnotationData) {
        val script = data.script
        val args = data.args.toArgs()
        val function = data.function
        if (function != "null") return
        val vars = script.script.engine.getBindings(ENGINE_SCOPE)
        val key = vars["key"]?.toString() ?: error("MessageType key in ${script.key} is null")
        object : MessageBuilder {
            override val key: String = key

            override fun build(
                damageType: DamageType,
                fightData: FightData,
                first: Boolean,
                type: Message.Type,
            ): Message {
                return messageCache.map.getOrPut(key) {
                    object : Message {
                        override val fightData: FightData = fightData

                        override fun sendTo(vararg players: Player) {
                            Pouvoir.scriptManager.invoke<Unit>(
                                script, "sendTo", parameters = arrayOf(players, fightData, first, type)
                            )
                        }

                        override fun plus(message: Message, type: Message.Type): Message {
                            return Pouvoir.scriptManager.invoke<Message>(
                                script, "plus", parameters = arrayOf(message, fightData, first, type)
                            ) ?: error("MessageType plus's returning value in ${script.key} is null")
                        }
                    }
                }
            }
        }.register()
        ASConfig.debug { console().sendLang("annotation-message-type-register", key) }
        script.onDeleted("MessageType-$key") {
            ASConfig.debug { console().sendLang("annotation-message-type-unregister", key) }
            AttributeSystem.messageBuilderManager.attack.remove(key)
            AttributeSystem.messageBuilderManager.defend.remove(key)
        }
    }
}