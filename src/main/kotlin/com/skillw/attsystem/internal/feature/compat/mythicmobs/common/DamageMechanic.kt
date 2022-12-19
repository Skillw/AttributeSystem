package com.skillw.attsystem.internal.feature.compat.mythicmobs.common

import io.lumine.mythic.api.adapters.AbstractEntity
import io.lumine.mythic.api.adapters.SkillAdapter
import io.lumine.mythic.api.config.MythicLineConfig
import io.lumine.mythic.api.skills.ITargetedEntitySkill
import io.lumine.mythic.api.skills.SkillMetadata
import io.lumine.mythic.api.skills.damage.DamageMetadata
import io.lumine.mythic.api.skills.placeholders.PlaceholderString
import org.bukkit.event.entity.EntityDamageEvent

/**
 * @className DamageMechanic
 *
 * @author Glom
 * @date 2022/12/6 7:23 Copyright 2022 user. All rights reserved.
 */
abstract class DamageMechanic(config: MythicLineConfig) : ITargetedEntitySkill {
    private var ignoresArmor = config.getBoolean(arrayOf("ignorearmor", "ia", "i"), false)
    private var preventImmunity = config.getBoolean(arrayOf("preventimmunity", "pi"), false)
    private var preventKnockback = config.getBoolean(arrayOf("preventknockback", "pkb", "pk"), false)
    private var ignoresEnchantments = config.getBoolean(arrayOf("ignoreenchantments", "ignoreenchants", "ie"), false)
    private var element: PlaceholderString? =
        PlaceholderString.of(config.getString(arrayOf("element", "e", "damagetype", "type"), null))
    private var cause: PlaceholderString =
        PlaceholderString.of(config.getString(arrayOf("damagecause", "dc", "cause"), "ENTITY_ATTACK"))


    protected fun doDamage(data: SkillMetadata, target: AbstractEntity?, amount: Double) {
        val caster = data.caster
        val meta = DamageMetadata(
            caster, amount,
            element?.get(data, target), ignoresArmor, preventImmunity, preventKnockback, ignoresEnchantments,
            EntityDamageEvent.DamageCause.valueOf(cause.get(data, target))
        )
        SkillAdapter.get().doDamage(meta, target)
    }
}