package com.skillw.attsystem.internal.feature.realizer.vanilla

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
        MinecraftVersion.minor <= 11300
    }


    private fun getSkillAPIHealth(player: Player): Int {
        return if (ASConfig.skillAPI) SkillAPI.getPlayerData(player).classes.stream()
            .mapToInt { aClass: PlayerClass -> aClass.health.toInt() }.sum() else 0
    }


    override fun newTask(entity: LivingEntity): (() -> Unit)? {
        val uuid = entity.uniqueId
        var value = value(entity)
        if (isLegacy) {
            value += default
        }
        if (entity is Player && (isLegacy || isEnableVanilla())) {
            value += getSkillAPIHealth(entity).toDouble()
        }
        if (!changed(uuid, value)) return null
        val modifier = genModifier(value)
        return if (entity !is Player || (!isLegacy && isEnableVanilla())) {
            {
                entity.getAttribute(attribute)?.run {
                    if (!isEnableVanilla()) clear()
                    else removeModifier(modifier)
                    addModifier(modifier)
                }
            }
        } else {
            if (value <= 0.0) {
                taboolib.common.platform.function.warning("Max Health value must bigger than 0.0! $entity $value")
                Throwable().printStackTrace()
                return null
            }
            {
                entity.apply {
                    getAttribute(BukkitAttribute.MAX_HEALTH)?.apply {
                        clear()
                    }
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