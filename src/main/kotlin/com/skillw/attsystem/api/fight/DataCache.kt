package com.skillw.attsystem.api.fight

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.AttrAPI.attribute
import com.skillw.attsystem.api.AttrAPI.getAttrData
import com.skillw.attsystem.api.AttrAPI.hasData
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.attsystem.internal.feature.compat.pouvoir.AttributePlaceHolder
import com.skillw.attsystem.internal.manager.ASConfig
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.platform.function.console
import taboolib.module.lang.sendWarn
import taboolib.module.nms.getI18nName

/**
 * @className DataCache
 *
 * @author Glom
 * @date 2023/1/22 18:00 Copyright 2023 user. All rights reserved.
 */
class DataCache(data: FightData? = null) {
    var data: FightData? = data
        set(value) {
            field = value
            field ?: return
            field!!["attacker-name"] = attackerName
            field!!["defender-name"] = defenderName
        }
    var attackerData: AttributeDataCompound? = null
    var defenderData: AttributeDataCompound? = null
    var attackerName: String = ASConfig.defaultAttackerName
    var defenderName: String = ASConfig.defaultDefenderName

    fun setData(other: DataCache) {
        other.attackerData?.let { attackerData = it }
        other.defenderData?.let { defenderData = it }
    }

    fun attacker(entity: LivingEntity?): DataCache {
        entity ?: return this
        if (!entity.hasData())
            AttributeSystem.attributeSystemAPI.update(entity)
        attackerData = entity.getAttrData()!!.clone()
        attackerName = (entity as? Player)?.displayName ?: entity.getI18nName()
        data ?: return this
        data!!["attacker"] = entity
        data!!["attacker-name"] = attackerName
        return this
    }

    fun defender(entity: LivingEntity?): DataCache {
        entity ?: return this
        if (!entity.hasData())
            AttributeSystem.attributeSystemAPI.update(entity)
        defenderData = entity.getAttrData()!!.clone()
        defenderName = (entity as? Player)?.displayName ?: entity.getI18nName()
        data ?: return this
        data!!["defender"] = entity
        data!!["defender-name"] = defenderName
        return this
    }

    fun attackerData(attKey: String, params: List<String>): String {
        val attribute = attribute(attKey)
        attribute ?: console().sendWarn("invalid-attribute", attKey)
        attribute ?: return "0.0"
        return attackerData?.let { AttributePlaceHolder.get(it, attribute, params) } ?: "0.0"
    }

    fun defenderData(attKey: String, params: List<String>): String {
        val attribute = attribute(attKey)
        attribute ?: console().sendWarn("invalid-attribute", attKey)
        attribute ?: return "0.0"
        return defenderData?.let { AttributePlaceHolder.get(it, attribute, params) } ?: "0.0"
    }
}