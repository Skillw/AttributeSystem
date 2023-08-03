package com.skillw.attsystem.internal.feature.personal

import com.skillw.attsystem.AttributeSystem.attributeDataManager
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.pouvoir.api.able.Keyable
import com.skillw.pouvoir.util.GsonUtils.decodeFromString
import com.skillw.pouvoir.util.GsonUtils.encodeJson
import org.bukkit.entity.Player
import java.util.*

/**
 * @className InitialAttrData
 *
 * @author Glom
 * @date 2023/8/1 18:07 Copyright 2023 user. All rights reserved.
 */
class InitialAttrData(override val key: UUID, val compound: AttributeDataCompound = AttributeDataCompound()) :
    Keyable<UUID> {
    companion object {
        @JvmStatic
        fun deserialize(uuid: UUID, str: String): InitialAttrData? {
            return InitialAttrData(uuid, AttributeDataCompound.fromMap(str.decodeFromString() ?: return null))
        }

        @JvmStatic
        fun fromPlayer(player: Player): InitialAttrData {
            return InitialAttrData(player.uniqueId, attributeDataManager[player.uniqueId] ?: AttributeDataCompound())
        }
    }

    fun serialize(): String {
        return compound.mapValues { it.value.serialize() }.encodeJson()
    }
}