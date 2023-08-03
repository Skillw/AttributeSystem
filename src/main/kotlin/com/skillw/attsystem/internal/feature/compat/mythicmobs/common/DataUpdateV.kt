package com.skillw.attsystem.internal.feature.compat.mythicmobs.common

import com.skillw.attsystem.api.AttrAPI.update
import com.skillw.pouvoir.util.EntityUtils.isAlive
import io.lumine.mythic.api.skills.INoTargetSkill
import io.lumine.mythic.api.skills.SkillMetadata
import io.lumine.mythic.api.skills.SkillResult
import io.lumine.mythic.core.logging.MythicLogger
import org.bukkit.entity.LivingEntity
import taboolib.module.nms.getI18nName

/**
 * @className AttributeDamageIV
 *
 * @author Glom
 * @date 2022/7/11 17:14 Copyright 2022 user. All rights reserved.
 */
internal object DataUpdateV : INoTargetSkill {
    override fun cast(data: SkillMetadata): SkillResult {
        val target = data.caster.entity.bukkitEntity
        if (target !is LivingEntity || !target.isAlive()) return SkillResult.CONDITION_FAILED
        target.update()
        MythicLogger.debug(
            MythicLogger.DebugLevel.MECHANIC,
            "+ DataUpdate Mechanic fired for {0}",
            target.getI18nName()
        )
        return SkillResult.SUCCESS
    }


}

