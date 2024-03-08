package com.skillw.attsystem.internal.command.sub

import com.skillw.attsystem.internal.command.ASCommand.soundSuccess
import com.skillw.attsystem.util.legacy.Mirror
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.submitAsync
import taboolib.module.lang.sendLang

object MirrorCommand {
    val report = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            sender.soundSuccess()
            submitAsync { Mirror.report(sender) }
        }
    }

    val clear = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            sender.soundSuccess()
            sender.sendLang("command-clear")
            Mirror.mirrorData.clear()
        }
    }
}