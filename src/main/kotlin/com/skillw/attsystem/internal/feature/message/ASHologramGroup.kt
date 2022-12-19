package com.skillw.attsystem.internal.feature.message

import com.skillw.attsystem.api.fight.FightData
import com.skillw.attsystem.api.fight.message.Message
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.pouvoir.api.hologram.HologramBuilder
import com.skillw.pouvoir.util.FileUtils.toMap
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common5.Coerce
import taboolib.module.chat.colored

/**
 * A s hologram group
 *
 * @constructor Create empty A s hologram group
 * @property texts
 * @property location
 * @property node
 */
class ASHologramGroup(val texts: MutableList<String>, private val location: Location, val node: String, override val fightData: FightData) : Message {

    private fun Any?.toDouble(): Double {
        return Coerce.toDouble(this)
    }
  
    override fun sendTo(vararg players: Player) {
        val map = fightData.handleMap(ASConfig["message"].getConfigurationSection(node)!!.toMap(),false)
        val begin = map["begin"] as Map<String,Any>
        val beginLocation = Location(location.world, begin["x"].toDouble(), begin["y"].toDouble(), begin["z"].toDouble())
        val end = map["end"] as Map<String,Any>
        val endLocation = Location(location.world, end["x"].toDouble(), end["y"].toDouble(), end["z"].toDouble())
        val stay = map["stay"].toDouble().toLong()
        val time = map["time"].toDouble().toInt()
        HologramBuilder(location.clone().add(beginLocation))
            .content(texts.colored().toMutableList())
            .stay(stay)
            .animation(time, location.clone().add(endLocation))
            .viewers(*players).build()
    }

    override fun plus(message: Message, type: Message.Type): Message {
        message as ASHologramGroup
        this.texts.addAll(message.texts)
        return this
    }
}
