package com.skillw.attsystem.internal.feature.compat.magic

import com.elmakers.mine.bukkit.action.builtin.DamageAction
import com.elmakers.mine.bukkit.api.action.CastContext
import com.elmakers.mine.bukkit.api.spell.SpellResult
import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.fight.FightData
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Damageable
import org.bukkit.entity.LivingEntity

class AttDamage() : DamageAction() {

    private var fightKey: String? = null

    override fun prepare(context: CastContext, parameters: ConfigurationSection) {
        super.prepare(context, parameters)
        this.fightKey = parameters.getString("fight-group")
    }

    override fun perform(context: CastContext): SpellResult {
        val entity = context.targetEntity
        if (entity != null && entity is Damageable && !entity.isDead() && entity is LivingEntity && fightKey != null) {
            val mage = context.mage
            elementalDamage =
                AttributeSystem.attributeSystemAPI.runFight(fightKey!!, FightData(mage.livingEntity!!, entity), true)
            return super.perform(context)
        }
        return SpellResult.NO_TARGET
    }
}