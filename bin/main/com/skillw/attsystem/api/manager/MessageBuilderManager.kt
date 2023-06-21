package com.skillw.attsystem.api.manager

import com.skillw.attsystem.api.fight.message.MessageBuilder
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.LowerKeyMap


/**
 * MessageType type manager
 *
 * 用于注册自定义消息类型
 *
 * 编写Message的实现类，并注册它的Builder以自定义消息类型
 *
 * @constructor Create empty MessageType type manager
 */
abstract class MessageBuilderManager : Manager {
    /** Attack */
    abstract val attack: LowerKeyMap<MessageBuilder>

    /** Defend */
    abstract val defend: LowerKeyMap<MessageBuilder>
}
