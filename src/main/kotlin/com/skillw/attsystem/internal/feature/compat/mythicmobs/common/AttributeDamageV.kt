package com.skillw.attsystem.internal.feature.compat.mythicmobs.common

import com.skillw.attsystem.AttributeSystem
import io.lumine.mythic.api.adapters.AbstractEntity
import io.lumine.mythic.api.config.MythicLineConfig
import io.lumine.mythic.api.skills.SkillMetadata
import io.lumine.mythic.api.skills.SkillResult
import io.lumine.mythic.api.skills.placeholders.PlaceholderString
import io.lumine.mythic.core.logging.MythicLogger
import org.bukkit.entity.LivingEntity

/**
 * @className AttributeDamageV
 *
 * @author Glom
 * @date 2022/7/11 17:14 Copyright 2022 user. All rights reserved.
 */
internal class AttributeDamageV(private val config: MythicLineConfig) :
    DamageMechanic(config) {
    val key: PlaceholderString =
        PlaceholderString.of(config.getString(arrayOf("key", "k"), "null"))

    override fun castAtEntity(data: SkillMetadata, targetAE: AbstractEntity): SkillResult {
        val caster = data.caster.entity.bukkitEntity
        val target = targetAE.bukkitEntity
        if (caster is LivingEntity && target is LivingEntity && !target.isDead) {
            val damage =
                AttributeSystem.attributeSystemAPI.playerAttackCal(
                    key.get(data, targetAE),
                    caster,
                    target
                ) {
                    it["power"] = data.power.toDouble()
                    config.entrySet().forEach { entry ->
                        it[entry.key] = entry.value
                    }
                }
            doDamage(data, targetAE, damage)
            MythicLogger.debug(
                MythicLogger.DebugLevel.MECHANIC,
                "+ AttributeDamageMechanic fired for {0} with {1} power",
                damage, data.power
            )
        }
        return SkillResult.SUCCESS
    }


}

