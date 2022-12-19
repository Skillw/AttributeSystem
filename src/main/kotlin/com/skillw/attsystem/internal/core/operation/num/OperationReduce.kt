package com.skillw.attsystem.internal.core.operation.num

import com.skillw.attsystem.api.operation.NumberOperation
import com.skillw.pouvoir.api.annotation.AutoRegister

@AutoRegister
object OperationReduce : NumberOperation("reduce") {
    override fun operate(a: Number, b: Number): Number {
        return a.toDouble() - b.toDouble()
    }
}
