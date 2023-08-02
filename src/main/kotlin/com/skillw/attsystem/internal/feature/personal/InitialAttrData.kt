package com.skillw.attsystem.internal.feature.personal

import com.skillw.attsystem.AttributeSystem.attributeDataManager
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.attsystem.internal.feature.database.ASContainer
import com.skillw.pouvoir.api.plugin.map.component.Keyable
import com.skillw.pouvoir.util.decodeFromString
import com.skillw.pouvoir.util.encodeJson
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

        @JvmStatic
        internal fun pushAttrData(player: Player) {
            val name = player.name
            ASContainer[name, "initial-attr-data"] = fromPlayer(player).serialize()
        }

        @JvmStatic
        internal fun pullAttrData(uuid: UUID): InitialAttrData? {
            val data = ASContainer[uuid.toString(), "initial-attr-data"] ?: return null
            if (data == "null") return null
            return deserialize(uuid, data)
        }
    }

    fun serialize(): String {
        return compound.map.mapValues { it.value.serialize() }.encodeJson()
    }
}