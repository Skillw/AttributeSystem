package com.skillw.attsystem.internal.command.sub

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.attsystem.internal.command.ASCommand.soundClick
import com.skillw.attsystem.internal.command.ASCommand.soundFail
import com.skillw.attsystem.internal.command.ASCommand.soundSuccess
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.pouvoir.util.getEntityRayHit
import com.skillw.pouvoir.util.soundClick
import com.skillw.pouvoir.util.soundFail
import com.skillw.pouvoir.util.soundSuccess
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.onlinePlayers
import taboolib.common.platform.function.submitAsync
import taboolib.module.chat.ComponentText
import taboolib.module.chat.colored
import taboolib.module.lang.sendLang
import taboolib.module.nms.getI18nName
import taboolib.module.nms.getName
import taboolib.platform.util.hasLore
import taboolib.platform.util.isAir
import taboolib.platform.util.sendLang

object AttributeStatsCommand {
    val stats = subCommand {
        dynamic(optional = true) {
            suggestion<ProxyCommandSender> { sender, _ ->
                sender.soundClick()
                onlinePlayers().map { it.name }
            }
            execute<ProxyCommandSender> { sender, _, argument ->
                Bukkit.getPlayer(argument)?.let {
                    sender.soundSuccess()
                    submitAsync {
                        sendStatText(sender, it)
                    }
                } ?: run {
                    sender.soundFail()
                    sender.sendLang("command-valid-player", argument)
                    return@execute
                }
            }
        }
        execute<ProxyPlayer> { sender, _, _ ->
            submitAsync {
                sender.soundSuccess()
                sendStatText(sender, sender.cast())
            }
        }
    }

    val itemstats = subCommand {
        dynamic {
            suggestion<ProxyCommandSender> { sender, _ ->
                sender.soundClick()
                onlinePlayers().map { it.name }
            }
            dynamic {
                suggestion<Player> { sender, _ ->
                    sender.soundClick()
                    AttributeSystem.equipmentDataManager[sender.uniqueId]?.map { it.key }
                }
                dynamic {
                    suggestion<Player> { sender, _ ->
                        sender.soundClick()
                        val list = ArrayList<String>()
                        AttributeSystem.equipmentDataManager[sender.uniqueId]?.values?.forEach { list.addAll(it.keys) }
                        list
                    }
                    execute<Player> { sender, context, slot ->
                        val player = Bukkit.getPlayer(context.argument(-2))
                        player ?: run {
                            sender.sendLang("command-valid-player", context.argument(-2))
                            return@execute
                        }
                        val source = context.argument(-1)
                        val itemStack = AttributeSystem.equipmentDataManager[player.uniqueId]?.get(source, slot)
                        itemStack ?: run {
                            sender.soundFail()
                            sender.sendLang("command-valid-item")
                            return@execute
                        }
                        if (itemStack.isAir() || !itemStack.hasLore()) return@execute
                        sender.soundSuccess()
                        submitAsync {
                            val data =
                                AttributeSystem.compiledAttrDataManager[player.uniqueId]?.get(
                                    AttributeSystem.equipmentDataManager.getSource(
                                        source,
                                        slot
                                    )
                                )?.eval(player) ?: AttributeDataCompound()
                            sendStatText(adaptPlayer(sender), player, itemStack.getName(), data, true)
                        }
                    }
                }
            }
        }
    }

    val entitystats = subCommand {
        execute<ProxyPlayer> { sender, _, _ ->
            val player = sender.cast<Player>()
            val entity = player.getEntityRayHit(10.0) as? LivingEntity
            entity ?: kotlin.run {
                player.soundFail()
                sender.sendLang("command-valid-entity")
                return@execute
            }
            player.soundSuccess()
            submitAsync {
                sendStatText(sender, entity)
            }
        }
    }

    private fun attributeStatusToJson(
        data: AttributeDataCompound,
        entity: LivingEntity,
        item: Boolean = false,
    ): ArrayList<ComponentText> {
        val attributes = AttributeSystem.attributeManager.attributes
        val list = ArrayList<ComponentText>()
        for (index in attributes.indices) {
            val attribute = attributes[index]
            if (!attribute.entity && !item) continue
            val status = data.getStatus(attribute) ?: continue
            val json = attribute.readPattern.stat(
                attribute,
                status,
                entity
            )
            list.add(json)
        }
        return list
    }

    private fun sendStatText(
        sender: ProxyCommandSender,
        entity: LivingEntity,
        name: String = (entity as? Player)?.displayName
            ?: ((if (entity.customName == null) entity.getI18nName() else entity.customName) ?: "null"),
        data: AttributeDataCompound = AttributeSystem.attributeDataManager[entity.uniqueId]
            ?: AttributeDataCompound(),
        item: Boolean = false,
    ) {
        val title = ASConfig.statsTitle.replace("{name}", name).replace("{player}", name).colored()
        sender.sendMessage(" ")
        sender.sendMessage(title)
        sender.sendMessage(" ")
        attributeStatusToJson(data, entity, item).forEach {
            it.sendTo(sender)
        }
        sender.sendMessage(" ")
        sender.sendMessage(ASConfig.statsEnd)
    }
}