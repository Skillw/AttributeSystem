package com.skillw.attsystem.api.read.status

import com.skillw.attsystem.api.read.operation.Operation
import com.skillw.attsystem.internal.core.read.BaseReadGroup
import com.skillw.pouvoir.api.plugin.map.LowerMap

/**
 * Status
 *
 * @constructor Create empty Status
 */
abstract class Status<A : Any>(val readGroup: BaseReadGroup<A>) : LowerMap<A>() {
    open fun operation(status: Status<*>): Status<A> {
        status as? Status<A> ?: return this
        for (key in status.keys) {
            if (this.containsKey(key)) {
                this.register(
                    key,
                    readGroup.matchers[key]?.operation?.operate(get(key) ?: continue, status[key] ?: continue)
                        ?: continue
                )
            } else {
                this.register(key, status[key] ?: continue)
            }
        }
        return this
    }

    fun getTotal(): A {
        return readGroup.getTotal()
    }

    /**
     * NumberOperation
     *
     * 做运算
     *
     * @param key (捕获组)键
     * @param value (捕获组)值
     * @param operation 运算操作
     * @return 运算结果(自身)
     */
    open fun operation(key: String, value: A, operation: Operation<A>): Status<A> {
        if (this.containsKey(key)) {
            this.register(key, operation.operate(get(key) ?: return this, value))
        } else {
            this.register(key, value)
        }
        return this
    }

    /**
     * Serialize
     *
     * @return 序列化结果
     */
    open fun serialize(): MutableMap<String, Any> {
        return HashMap(this)
    }

    /**
     * Clone
     *
     * @return 复制结果
     */
    abstract override fun clone(): Status<A>

}
