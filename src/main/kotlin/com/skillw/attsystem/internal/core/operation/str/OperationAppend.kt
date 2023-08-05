package com.skillw.attsystem.internal.core.operation.str

import com.skillw.attsystem.api.read.operation.StringOperation
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister
import taboolib.common.util.join

@AutoRegister
object OperationAppend : StringOperation("append") {
    override fun operate(a: String, b: String): String {
        return join(arrayOf(a, b), separator = ASConfig.strAppendSeparator)
    }

}