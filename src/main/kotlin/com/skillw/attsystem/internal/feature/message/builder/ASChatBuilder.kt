package com.skillw.attsystem.internal.feature.message.builder

import com.skillw.attsystem.AttributeSystem.message
import com.skillw.attsystem.api.fight.DamageType
import com.skillw.attsystem.api.fight.FightData
import com.skillw.attsystem.api.fight.message.Message
import com.skillw.attsystem.api.fight.message.MessageBuilder
import com.skillw.attsystem.internal.feature.message.ASChat
import com.skillw.pouvoir.api.annotation.AutoRegister

@AutoRegister
object ASChatBuilder : MessageBuilder {

    override val key: String = "chat"


    override fun build(damageType: DamageType, fightData: FightData, first: Boolean, type: Message.Type): Message {
        val typeStr = type.name.lowercase()
        val typeText = fightData.handleStr(damageType["$typeStr-chat"].toString().replace("{name}", damageType.name))
        val text = if (first) fightData.handleStr(
            message.getString("fight-message.chat.$typeStr.text")
                ?.replace("{message}", typeText) ?: typeText
        )
        else typeText
        return ASChat(StringBuilder(text), fightData)
    }
}
