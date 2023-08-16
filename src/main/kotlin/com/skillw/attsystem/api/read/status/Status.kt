package com.skillw.attsystem.api.read.status

import com.skillw.attsystem.api.attribute.Attribute
import com.skillw.attsystem.internal.core.read.BaseReadGroup
import com.skillw.pouvoir.api.feature.operation.Operation
import com.skillw.pouvoir.api.plugin.map.LowerMap
import org.bukkit.entity.LivingEntity

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
                    readGroup.operations()[key]?.operate(get(key) ?: continue, status[key] ?: continue)
                        ?: continue
                )
            } else {
                this.register(key, status[key] ?: continue)
            }
        }
        return this
    }

    open fun getTotal(attribute: Attribute, entity: LivingEntity): A? = readGroup.getTotal(attribute, this, entity)
    open fun getMin(attribute: Attribute, entity: LivingEntity?): A? = readGroup.getMin(attribute, this, entity)
    open fun getMax(attribute: Attribute, entity: LivingEntity?): A? = readGroup.getMax(attribute, this, entity)

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
    public abstract override fun clone(): Status<A>

}
