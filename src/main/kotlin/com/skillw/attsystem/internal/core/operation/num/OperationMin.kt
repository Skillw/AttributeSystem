package com.skillw.attsystem.internal.core.operation.num

import com.skillw.attsystem.api.operation.NumberOperation
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister
import kotlin.math.min

@AutoRegister
object OperationMin : NumberOperation("min", ">") {
    override fun operate(a: Number, b: Number): Number {
        return min(a.toDouble(), b.toDouble())
    }
}
