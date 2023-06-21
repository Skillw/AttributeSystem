package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.condition.BaseCondition
import com.skillw.attsystem.api.event.ConditionEvent
import com.skillw.attsystem.api.manager.ConditionManager
import org.bukkit.entity.LivingEntity
import java.util.regex.Matcher

object ConditionManagerImpl : ConditionManager() {
    override val key = "ConditionManager"
    override val priority: Int = 7
    override val subPouvoir = AttributeSystem

    override fun onEnable() {
        onReload()
    }

    override fun onReload() {
        this.entries.filter { it.value.release }.forEach { this.remove(it.key) }
    }

    private fun matches(str: String, type: BaseCondition.ConditionType): Pair<Matcher, BaseCondition>? {
        for ((_, condition) in this) {
            if (!condition.isType(type)) continue
            for (name in condition.patterns) {
                val matcher = name.matcher(str)
                if (matcher.find()) {
                    return matcher to condition
                }
            }
        }
        return null
    }

    override fun lineConditions(slot: String?, requirements: String, entity: LivingEntity?): Boolean {
        val separator = ASConfig.lineConditionSeparator
        val array: List<String> =
            if (requirements.contains(separator)) {
                requirements.split(separator)
            } else {
                listOf(requirements)
            }
        return array.all { AttributeSystem.conditionManager.conditionLine(slot, entity, it) }
    }

    private fun condition(
        slot: String?,
        livingEntity: LivingEntity?,
        str: String,
        type: BaseCondition.ConditionType,
    ): Boolean {
        val pair = this.matches(str, type) ?: return true
        val (matcher, condition1) = pair
        val pass = condition1.condition(slot, livingEntity, matcher, str)
        val event = ConditionEvent(condition1, livingEntity, matcher, str, pass)
        event.call()
        return event.pass
    }

    override fun conditionNBT(slot: String?, entity: LivingEntity?, map: Map<String, Any>): Set<String> {
        val limits = HashSet<String>()
        for ((path, conditions) in map) {
            conditions as? Map<String, Any> ?: continue
            inner@ for ((key, parameters) in conditions) {
                parameters as? Map<String, Any> ?: continue
                val condition = get(key) ?: continue
                if (!condition.conditionNBT(slot, entity, parameters)) {
                    limits.add(path.replace("$", "."))
                    break@inner
                }
            }
        }
        return limits
    }

    override fun conditionLine(slot: String?, entity: LivingEntity?, str: String): Boolean {
        return condition(slot, entity, str, BaseCondition.ConditionType.LINE)
    }

    override fun conditionStrings(slot: String?, entity: LivingEntity?, strings: Collection<String>): Boolean {
        return strings.all { str ->
            if (ASConfig.lineConditionPattern.matcher(str).find()) return@all true
            condition(slot, entity, str, BaseCondition.ConditionType.STRINGS)
        }
    }


}
