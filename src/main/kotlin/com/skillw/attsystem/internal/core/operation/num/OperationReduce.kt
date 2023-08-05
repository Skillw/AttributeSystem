package com.skillw.attsystem.internal.core.operation.num

import com.skillw.attsystem.api.read.operation.NumberOperation
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister

@AutoRegister
object OperationReduce : NumberOperation("reduce", "-") {
    override fun operate(a: Number, b: Number): Number {
        return a.toDouble() - b.toDouble()
    }
}
