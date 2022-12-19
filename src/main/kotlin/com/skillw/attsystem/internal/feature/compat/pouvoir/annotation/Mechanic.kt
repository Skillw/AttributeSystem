package com.skillw.attsystem.internal.feature.compat.pouvoir.annotation

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.fight.DamageType
import com.skillw.attsystem.api.fight.FightData
import com.skillw.attsystem.api.fight.mechanic.Mechanic
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.pouvoir.Pouvoir
import com.skillw.pouvoir.api.annotation.AutoRegister
import com.skillw.pouvoir.api.script.annotation.ScriptAnnotation
import com.skillw.pouvoir.api.script.annotation.ScriptAnnotationData
import com.skillw.pouvoir.util.StringUtils.toArgs
import taboolib.common.platform.function.console
import taboolib.module.lang.sendLang

/**
 * Mechanic 机制注解 机制键(可选 若无则为函数名)
 *
 * @constructor Mechanic Key(Optional)
 */
@AutoRegister
object Mechanic : ScriptAnnotation("Mechanic") {
    override fun handle(data: ScriptAnnotationData) {
        val script = data.script
        val args = data.args.toArgs()
        val function = data.function
        val key = if (args.isEmpty() || args[0] == "") function else args[0]
        object : Mechanic(key) {
            override fun exec(fightData: FightData, context: Map<String, Any>, damageType: DamageType): Any? {
                return Pouvoir.scriptManager.invoke(
                    script,
                    function,
                    parameters = arrayOf(fightData, context, damageType)
                )
            }
        }.register()
        ASConfig.debug { console().sendLang("annotation-mechanic-register", key) }
        script.onDeleted("Mechanic-$key") {
            ASConfig.debug { console().sendLang("annotation-mechanic-unregister", key) }
            AttributeSystem.mechanicManager.remove(key)
        }
    }
}