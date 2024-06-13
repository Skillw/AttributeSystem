package com.skillw.attsystem.internal.feature.realizer.vanilla

import com.skillw.attsystem.api.event.VanillaAttributeUpdateEvent
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.pouvoir.util.attribute.BukkitAttribute
import com.skillw.pouvoir.util.attribute.clear
import com.skillw.pouvoir.util.attribute.getAttribute
import com.sucy.skill.SkillAPI
import com.sucy.skill.api.player.PlayerClass
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common5.Coerce
import taboolib.module.nms.MinecraftVersion

internal object MaxHealthTaskBuilder : VanillaAttTaskBuilder("max-health", BukkitAttribute.MAX_HEALTH) {

    val default
        get() = Coerce.toDouble(config["default"])

    private val isLegacy by lazy {
        MinecraftVersion.majorLegacy <= 11300
    }


    private fun getSkillAPIHealth(player: Player): Int {
        return if (ASConfig.skillAPI) SkillAPI.getPlayerData(player).classes.stream()
            .mapToInt { aClass: PlayerClass -> aClass.health.toInt() }.sum() else 0
    }


    override fun newTask(entity: LivingEntity): (() -> Unit)? {
        val uuid = entity.uniqueId
        var value = value(entity)
        val vanilla = isEnableVanilla()
        if (entity is Player && isLegacy) {
            value += default
        }
        if (entity is Player && (isLegacy || vanilla)) {
            value += getSkillAPIHealth(entity).toDouble()
        }
        if (!changed(uuid, value)) return null
        val modifier = genModifier(value)
        return if (entity !is Player || (!isLegacy && vanilla)) {
            {
                entity.getAttribute(attribute)?.run {
                    if (!isEnableVanilla()) clear()
                    else removeModifier(modifier)
                    addModifier(modifier)
                    VanillaAttributeUpdateEvent(entity, BukkitAttribute.MAX_HEALTH, if(isEnableVanilla()) value + 20 else value).call()
                }
            }
        } else {
            if (value <= 0.0) {
                taboolib.common.platform.function.warning("Max Health value must bigger than 0.0! $entity $value")
                return null
            }
            {
                entity.apply {
                    getAttribute(BukkitAttribute.MAX_HEALTH)?.apply {
                        clear()
                    }
                    VanillaAttributeUpdateEvent(entity, BukkitAttribute.MAX_HEALTH, value).call()
                    maxHealth = value
                }
            }
        }
    }

    override fun unrealize(entity: LivingEntity) {
        super.unrealize(entity)
        entity.maxHealth = default
    }

    override fun onDisable() {

    }
}