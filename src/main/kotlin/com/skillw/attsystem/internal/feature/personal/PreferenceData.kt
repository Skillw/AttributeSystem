package com.skillw.attsystem.internal.feature.personal

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.pouvoir.api.able.Registrable
import java.util.*

class PreferenceData(override val key: UUID) : Registrable<UUID> {
    var attacking = ASConfig.defaultAttackMessageType
    var defensive = ASConfig.defaultDefendMessageType
    var regainHolo = ASConfig.defaultRegainHolo

    val default: Boolean
        get() = attacking == ASConfig.defaultAttackMessageType &&
                defensive == ASConfig.defaultDefendMessageType &&
                regainHolo == ASConfig.defaultRegainHolo


    fun default() {
        attacking = ASConfig.defaultAttackMessageType
        defensive = ASConfig.defaultDefendMessageType
        regainHolo = ASConfig.defaultRegainHolo
    }

    companion object {
        @JvmStatic
        fun deserialize(uuid: UUID, json: String): PreferenceData? {
            val array = json.split(";")
            if (array.isEmpty() || array.size < 3) return null
            val preferenceData = PreferenceData(uuid)
            preferenceData.attacking = array[0]
            preferenceData.defensive = array[1]
            preferenceData.regainHolo = array[2].toBoolean()
            return preferenceData
        }
    }

    fun serialize(): String {
        return "${attacking};${defensive};$regainHolo"
    }

    override fun register() {
        AttributeSystem.personalManager.registerPreferenceData(this)
    }
}
