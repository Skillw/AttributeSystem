package com.skillw.attsystem.internal.core.operation.str

import com.skillw.attsystem.api.operation.StringOperation
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister

@AutoRegister
object OperationSkip : StringOperation("skip") {
    override fun operate(a: String, b: String): String {
        return a
    }
}