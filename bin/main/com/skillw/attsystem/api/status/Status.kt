package com.skillw.attsystem.api.status

import org.bukkit.configuration.serialization.ConfigurationSerializable

/**
 * Status
 *
 * @constructor Create empty Status
 */
interface Status<A : Any> : ConfigurationSerializable {
    /**
     * 运算操作
     *
     * @param status 属性状态
     * @return 运算结果
     */
    fun operation(status: Status<*>): Status<A>

    /**
     * Clone
     *
     * @return 复制结果
     */
    fun clone(): Status<A>
}
