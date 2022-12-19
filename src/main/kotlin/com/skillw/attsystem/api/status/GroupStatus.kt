package com.skillw.attsystem.api.status

import com.skillw.attsystem.api.operation.Operation
import com.skillw.attsystem.internal.core.read.ReadGroup
import com.skillw.pouvoir.api.map.LowerMap

/**
 * Status
 *
 * @constructor Create empty Status
 */
abstract class GroupStatus<A : Any>(val readGroup: ReadGroup<A>) : Status<A>, LowerMap<A>() {


    override fun operation(status: Status<*>): Status<A> {
        status as? GroupStatus<A> ?: return this
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
    fun operation(key: String, value: A, operation: Operation<A>): GroupStatus<A> {
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
    override fun serialize(): MutableMap<String, Any> {
        return HashMap(this.map)
    }

    /**
     * Clone
     *
     * @return 复制结果
     */
    abstract override fun clone(): GroupStatus<A>
}
