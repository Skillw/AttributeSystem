package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.fight.message.MessageBuilder
import com.skillw.attsystem.api.manager.MessageBuilderManager
import com.skillw.pouvoir.api.map.LowerKeyMap


/**
 * MessageType type manager
 *
 * @constructor Create empty MessageType type manager
 */
object MessageBuilderManagerImpl : MessageBuilderManager() {
    override val key = "MessageBuilderManager"
    override val priority: Int = 3
    override val subPouvoir = AttributeSystem
    override val attack = LowerKeyMap<MessageBuilder>()
    override val defend = LowerKeyMap<MessageBuilder>()
}
