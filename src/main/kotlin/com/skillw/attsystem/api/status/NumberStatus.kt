package com.skillw.attsystem.api.status

import com.skillw.attsystem.api.operation.NumberOperation
import com.skillw.attsystem.internal.core.read.ReadGroup

/**
 * Number status
 *
 * @constructor Create empty Number status
 * @property numberReader
 */
class NumberStatus(numberReader: ReadGroup<Double>) : GroupStatus<Double>(numberReader) {

    override fun clone(): NumberStatus {
        val status = NumberStatus(readGroup)
        this.forEach {
            status.register(it.key, it.value)
        }
        return status
    }

    override fun get(key: String): Double {
        return super.get(key) ?: 0.0
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
                this.register(key, operation.operate(get(key), status[key]).toDouble())
            } else {
                this.register(key, status[key])
            }
        }
        return this
    }


}
