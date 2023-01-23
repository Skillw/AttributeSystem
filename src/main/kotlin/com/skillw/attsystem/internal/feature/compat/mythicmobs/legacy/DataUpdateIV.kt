package com.skillw.attsystem.internal.feature.compat.mythicmobs.legacy

import com.skillw.attsystem.api.AttrAPI.updateAttr
import com.skillw.pouvoir.util.isAlive
import io.lumine.xikage.mythicmobs.io.MythicLineConfig
import io.lumine.xikage.mythicmobs.logging.MythicLogger
import io.lumine.xikage.mythicmobs.skills.INoTargetSkill
import io.lumine.xikage.mythicmobs.skills.SkillMechanic
import io.lumine.xikage.mythicmobs.skills.SkillMetadata
import org.bukkit.entity.LivingEntity
import taboolib.module.nms.getI18nName

/**
 * @className AttributeDamageIV
 *
 * @author Glom
 * @date 2022/7/11 17:14 Copyright 2022 user. All rights reserved.
 */
internal class DataUpdateIV(skill: String, mlc: MythicLineConfig) : SkillMechanic(skill, mlc), INoTargetSkill {
    override fun cast(data: SkillMetadata): Boolean {
        val target = data.caster.entity.bukkitEntity
        if (target !is LivingEntity || !target.isAlive()) return false
        target.updateAttr()
        MythicLogger.debug(
            MythicLogger.DebugLevel.MECHANIC,
            "+ DataUpdate Mechanic fired for {0}",
            target.getI18nName()
        )
        return true
    }


}

