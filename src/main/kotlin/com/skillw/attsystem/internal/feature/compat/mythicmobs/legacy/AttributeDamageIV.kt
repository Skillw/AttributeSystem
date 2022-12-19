package com.skillw.attsystem.internal.feature.compat.mythicmobs.legacy

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.internal.manager.AttributeSystemAPIImpl.skipNextDamageCal
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity
import io.lumine.xikage.mythicmobs.io.MythicLineConfig
import io.lumine.xikage.mythicmobs.logging.MythicLogger
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill
import io.lumine.xikage.mythicmobs.skills.SkillMetadata
import io.lumine.xikage.mythicmobs.skills.mechanics.DamageMechanic
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderString
import org.bukkit.entity.LivingEntity

/**
 * @className AttributeDamageIV
 *
 * @author Glom
 * @date 2022/7/11 17:14 Copyright 2022 user. All rights reserved.
 */
internal class AttributeDamageIV(line: String?, private val mlc: MythicLineConfig) :
    DamageMechanic(line, mlc), ITargetedEntitySkill {
    val key: PlaceholderString = PlaceholderString.of(mlc.getString(arrayOf("key", "k"), "null", *arrayOfNulls(0)))

    override fun castAtEntity(data: SkillMetadata, targetAE: AbstractEntity): Boolean {
        val caster = data.caster.entity.bukkitEntity
        val target = targetAE.bukkitEntity
        return if (caster is LivingEntity && target is LivingEntity && !target.isDead) {
            val damage =
                AttributeSystem.attributeSystemAPI.playerAttackCal(
                    key.get(data, targetAE),
                    caster,
                    target
                ) {
                    it["power"] = data.power.toDouble()
                    mlc.entrySet().forEach { entry ->
                        it[entry.key] = entry.value
                    }
                }
            skipNextDamageCal()
            doDamage(data.caster, targetAE, damage)
            MythicLogger.debug(
                MythicLogger.DebugLevel.MECHANIC,
                "+ AttributeDamageMechanic fired for {0} with {1} power",
                damage, data.power
            )
            true
        } else {
            false
        }
    }


}

