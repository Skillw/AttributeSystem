package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.manager.PersonalManager
import com.skillw.attsystem.internal.feature.personal.InitialAttrData
import com.skillw.attsystem.internal.feature.personal.PreferenceData
import com.skillw.pouvoir.Pouvoir.containerManager
import com.skillw.pouvoir.api.map.KeyMap
import com.skillw.pouvoir.api.script.ScriptTool
import com.skillw.pouvoir.util.EntityUtils.player
import org.bukkit.entity.Player
import java.util.*

object PersonalManagerImpl : PersonalManager() {
    override val key = "PersonalManager"
    override val priority: Int = 12
    override val subPouvoir = AttributeSystem
    override val isPreferenceEnable: Boolean
        get() = ASConfig.isPreferenceEnable

    val preferenceData = KeyMap<UUID, PreferenceData>()

    override fun onDisable() {
        preferenceData.forEach {
            val player = it.key.player() ?: return
            pushData(player)
        }
    }

    override fun getPreference(key: UUID): PreferenceData {
        preferenceData.run {
            if (!containsKey(key)) {
                this[key] = PreferenceData(key)
            }
            if (!isPreferenceEnable) {
                if (get(key)!!.default) {
                    return get(key)!!
                } else if (containsKey(key)) {
                    get(key)!!.default()
                }
            }
            return get(key)!!
        }
    }

    override fun registerPreferenceData(data: PreferenceData) {
        preferenceData.register(data)
    }

    override fun pushData(player: Player) {
        val name = player.name
        if (isPreferenceEnable)
            containerManager[name, "personal-data"] = preferenceData[player.uniqueId]?.serialize()
        containerManager[name, "initial-attr-data"] = InitialAttrData.fromPlayer(player).serialize()
    }

    override fun pullPreferenceData(uuid: UUID): PreferenceData? {
        return if (isPreferenceEnable)
            PreferenceData.deserialize(uuid, containerManager[uuid.toString(), "preference-data"] ?: return null)
        else PreferenceData(uuid)
    }

    override fun pullInitialAttrData(uuid: UUID): InitialAttrData? {
        val data = containerManager[uuid.toString(), "initial-attr-data"] ?: return null
        if (data == "null") return null
        return InitialAttrData.deserialize(uuid, data)
    }

    override fun hasPreferenceData(player: Player): Boolean {
        return (ScriptTool.get(player, "personal-data") ?: "null") != "null"
    }


}
