package com.skillw.attsystem.internal.feature.compat.pouvoir.annotation

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.operation.StringOperation
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.pouvoir.Pouvoir
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister
import com.skillw.pouvoir.api.script.annotation.ScriptAnnotation
import com.skillw.pouvoir.api.script.annotation.ScriptAnnotationData
import com.skillw.pouvoir.util.toArgs
import taboolib.common.platform.function.console
import taboolib.module.lang.sendLang

/**
 * StringOperation 运算操作注解 运算操作键 (可选 若无则为函数名)
 *
 * @constructor StringOperation Key(Optional)
 */
@AutoRegister
internal object StringOperation : ScriptAnnotation("StringOperation") {
    override fun handle(data: ScriptAnnotationData) {
        val script = data.script
        val args = data.args.toArgs()
        val function = data.function
        val key = if (args.isEmpty() || args[0] == "") function else args[0]
        object : StringOperation(key) {
            override fun operate(a: String, b: String): String {
                return Pouvoir.scriptManager.invoke<String>(
                    script, function, parameters = arrayOf(a, b)
                ).toString()
            }
        }.register()
        ASConfig.debug { console().sendLang("annotation-string-operation-register", key) }
        script.onDeleted("StringOperation-$key") {
            ASConfig.debug { console().sendLang("annotation-string-operation-unregister", key) }
            AttributeSystem.operationManager.remove(key)
        }
    }
}