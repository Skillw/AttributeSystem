package com.skillw.attsystem.internal.core.operation.num

import com.skillw.attsystem.api.operation.NumberOperation
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister
import kotlin.math.max

@AutoRegister
object OperationMax : NumberOperation("max", "<") {
    override fun operate(a: Number, b: Number): Number {
        return max(a.toDouble(), b.toDouble())
    }
}
