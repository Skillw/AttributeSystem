package com.skillw.attsystem.api.fight.message

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.fight.DamageType
import com.skillw.attsystem.api.fight.FightData
import com.skillw.pouvoir.api.able.Registrable

/**
 * @className MessageBuilder
 *
 * @author Glom
 * @date 2022/8/1 4:32 Copyright 2022 user. All rights reserved.
 */
interface MessageBuilder : Registrable<String> {
    fun build(damageType: DamageType, fightData: FightData, first: Boolean, type: Message.Type): Message
    override fun register() {
        AttributeSystem.messageBuilderManager.attack.register(this)
        AttributeSystem.messageBuilderManager.defend.register(this)
    }
}