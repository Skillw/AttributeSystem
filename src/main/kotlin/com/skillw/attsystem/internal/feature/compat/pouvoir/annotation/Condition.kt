package com.skillw.attsystem.internal.feature.compat.pouvoir.annotation

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.condition.BaseCondition
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.pouvoir.Pouvoir
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister
import com.skillw.pouvoir.api.script.annotation.ScriptAnnotation
import com.skillw.pouvoir.api.script.annotation.ScriptAnnotationData
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.function.console
import taboolib.common5.Coerce
import taboolib.module.configuration.util.asMap
import taboolib.module.lang.sendLang
import java.util.regex.Matcher
import javax.script.ScriptContext.ENGINE_SCOPE

/**
 * Condition
 *
 * 条件注解 条件键 条件类型 条件名 （含正则）
 *
 * @constructor Condition Key String
 */
@AutoRegister
internal object Condition : ScriptAnnotation("Condition", fileAnnotation = true) {
    override fun handle(data: ScriptAnnotationData) {
        val script = data.script
        val function = data.function
        if (function != "null") return
        val vars = script.script.engine.getBindings(ENGINE_SCOPE)
        val key = vars["key"]?.toString() ?: error("Condition key in ${script.key} is null")
        val type = Coerce.toEnum(
            vars["type"],
            BaseCondition.ConditionType::class.java,
            BaseCondition.ConditionType.ALL
        )
        val names = HashSet<String>().apply {
            addAll(vars["names"].asMap().values.map { it.toString() })
        }
        object : BaseCondition(key, names, type) {
            override fun condition(slot: String?, entity: LivingEntity?, matcher: Matcher, text: String): Boolean {
                return Pouvoir.scriptManager.invoke<Boolean>(
                    script, "condition", parameters = arrayOf(slot, entity, matcher, text)
                ) ?: true
            }

            override fun conditionNBT(slot: String?, entity: LivingEntity?, map: Map<String, Any>): Boolean {
                return Pouvoir.scriptManager.invoke<Boolean>(
                    script, "conditionNBT", parameters = arrayOf(slot, entity, map)
                ) ?: true
            }
        }.register()
        ASConfig.debug { console().sendLang("annotation-condition-register", key) }
        script.onDeleted("Condition-$key") {
            ASConfig.debug { console().sendLang("annotation-condition-unregister", key) }
            AttributeSystem.conditionManager.remove(key)
        }
    }
}