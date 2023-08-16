package com.skillw.attsystem.api.read.status

import com.skillw.pouvoir.api.feature.operation.NumberOperation
import com.skillw.attsystem.internal.core.read.BaseReadGroup

/**
 * Number status
 *
 * @constructor Create empty Number status
 * @property numberReader
 */
class NumberStatus(numberReader: BaseReadGroup<Double>) : Status<Double>(numberReader) {

    override fun clone(): NumberStatus {
        val attributeStatus = NumberStatus(readGroup)
        this.forEach {
            attributeStatus.register(it.key, it.value)
        }
        return attributeStatus
    }


    /**
     * NumberOperation
     *
     * @param status 属性状态
     * @param operation 运算操作
     * @return 运算结果(自身)
     */
    fun operation(status: NumberStatus, operation: NumberOperation): NumberStatus {
        for (key in status.keys) {
            if (this.containsKey(key)) {
                this.register(key, operation.operate(get(key)!!, status[key]!!).toDouble())
            } else {
                this.register(key, status[key]!!)
            }
        }
        return this
    }
}
