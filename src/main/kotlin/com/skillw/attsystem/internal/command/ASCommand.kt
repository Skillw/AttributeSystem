package com.skillw.attsystem.internal.command

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.internal.command.sub.AttributeStatsCommand
import com.skillw.attsystem.internal.command.sub.MirrorCommand
import com.skillw.pouvoir.util.soundClick
import com.skillw.pouvoir.util.soundFail
import com.skillw.pouvoir.util.soundSuccess
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.pluginVersion
import taboolib.module.chat.colored
import taboolib.module.lang.sendLang

@CommandHeader(name = "as", permission = "as.command")
object ASCommand {
    internal fun ProxyCommandSender.soundSuccess() {
        (this.origin as? Player?)?.soundSuccess()
    }

    internal fun ProxyCommandSender.soundFail() {
        (this.origin as? Player?)?.soundFail()
    }

    internal fun ProxyCommandSender.soundClick() {
        (this.origin as? Player?)?.soundClick()
    }

    @CommandBody
    val main = mainCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            sender.sendLang("command-info")
            sender.soundSuccess()
        }
        incorrectCommand { sender, _, _, _ ->
            sender.sendLang("wrong-command")
            sender.soundFail()
        }
        incorrectSender { sender, _ ->
            sender.sendLang("wrong-sender")
            sender.soundFail()
        }
    }

    @CommandBody(permission = "as.command.help")
    val help = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            sender.soundSuccess()
            sender.sendLang("command-info")
        }
    }

    @CommandBody(permission = "as.command.info")
    val info = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            sender.soundSuccess()
            sender.sendMessage("&aAttributeSystem &9v$pluginVersion &6By Glom_".colored())
        }
    }

    @CommandBody(permission = "as.command.reload")
    val reload = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            sender.soundSuccess()
            AttributeSystem.reload()
            sender.sendLang("command-reload")
        }
    }

    @CommandBody(permission = "as.command.disable")
    val disable = subCommand {
        dynamic {
            execute<ProxyCommandSender> { sender, _, argument ->
                sender.soundFail()
                if (argument != "confirm") return@execute
                Bukkit.getScheduler().cancelTasks(AttributeSystem.plugin)
                Bukkit.getPluginManager().disablePlugin(AttributeSystem.plugin)
                sender.sendLang("command-disable")
            }
        }
        execute<ProxyCommandSender> { sender, _, _ ->
            sender.soundFail()
            sender.sendLang("command-disable-warning")
        }
    }

    @CommandBody(permission = "as.command.report")
    val report = MirrorCommand.report

    @CommandBody(permission = "as.command.clear")
    val clear = MirrorCommand.clear

    @CommandBody(permission = "as.command.stats")
    val stats = AttributeStatsCommand.stats

    @CommandBody(permission = "as.command.stats")
    val itemstats = AttributeStatsCommand.itemstats

    @CommandBody(permission = "as.command.stats")
    val entitystats = AttributeStatsCommand.entitystats
}
