package com.skillw.attsystem.internal.feature.message.builder

import com.skillw.attsystem.AttributeSystem.message
import com.skillw.attsystem.api.fight.DamageType
import com.skillw.attsystem.api.fight.FightData
import com.skillw.attsystem.api.fight.message.Message
import com.skillw.attsystem.api.fight.message.MessageBuilder
import com.skillw.attsystem.internal.feature.message.ASActionBar
import com.skillw.pouvoir.api.annotation.AutoRegister

@AutoRegister
object ASActionBarBuilder : MessageBuilder {

    override val key: String = "action_bar"

    override fun build(damageType: DamageType, fightData: FightData, first: Boolean, type: Message.Type): Message {
        val typeStr = type.name.lowercase()
        val typeText =
            fightData.handleStr(damageType["$typeStr-action-bar"].toString().replace("{name}", damageType.name))
        val text =
            if (first) fightData.handleStr(
                message.getString("fight-message.action-bar.$typeStr.text")
                    ?.replace("{message}", typeText)
                    ?: typeText
            )
            else typeText

        return ASActionBar(StringBuilder(text), fightData)
    }
}
