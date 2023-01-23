package com.skillw.attsystem.internal.feature.compat.pouvoir.annotation

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.operation.NumberOperation
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.pouvoir.Pouvoir
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister
import com.skillw.pouvoir.api.script.annotation.ScriptAnnotation
import com.skillw.pouvoir.api.script.annotation.ScriptAnnotationData
import com.skillw.pouvoir.util.toArgs
import taboolib.common.platform.function.console
import taboolib.common5.Coerce
import taboolib.module.lang.sendLang

/**
 * NumberOperation 运算操作注解 运算操作键 (可选 若无则为函数名)
 *
 * @constructor NumberOperation Key(Optional)
 */
@AutoRegister
internal object NumberOperation : ScriptAnnotation("NumberOperation") {
    override fun handle(data: ScriptAnnotationData) {
        val script = data.script
        val args = data.args.toArgs()
        val function = data.function
        val key = if (args.isEmpty() || args[0] == "") function else args[0]
        object : NumberOperation(key) {
            override fun operate(a: Number, b: Number): Number {
                return Coerce.toDouble(
                    Pouvoir.scriptManager.invoke(
                        script, function, parameters = arrayOf(a, b)
                    )
                )
            }
        }.register()
        ASConfig.debug { console().sendLang("annotation-number-operation-register", key) }
        script.onDeleted("NumberOperation-$key") {
            ASConfig.debug { console().sendLang("annotation-number-operation-unregister", key) }
            AttributeSystem.operationManager.remove(key)
        }
    }
}