package com.skillw.attsystem.internal.feature.personal

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.pouvoir.api.able.Registrable
import java.util.*

class PersonalData(override val key: UUID) : Registrable<UUID> {
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
        fun fromJson(json: String, uuid: UUID): PersonalData? {
            val array = json.split(";")
            if (array.isEmpty() || array.size < 3) return null
            val personalData = PersonalData(uuid)
            personalData.attacking = array[0]
            personalData.defensive = array[1]
            personalData.regainHolo = array[2].toBoolean()
            return personalData
        }
    }

    override fun toString(): String {
        return "${attacking};${defensive};$regainHolo"
    }

    override fun register() {
        AttributeSystem.personalManager.register(this)
    }
}
