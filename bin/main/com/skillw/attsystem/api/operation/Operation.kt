package com.skillw.attsystem.api.operation

import com.skillw.attsystem.AttributeSystem
import com.skillw.pouvoir.api.able.Registrable

/**
 * @className NumberOperation
 *
 * @author Glom
 * @date 2022/7/19 12:38 Copyright 2022 user. All rights reserved.
 */
interface Operation<T> : Registrable<String> {
    /** 是否在重载时删除 */
    var release: Boolean

    /**
     * 做运算
     *
     * @param a 对象a
     * @param b 对象b
     * @return 运算结果
     */
    fun operate(a: T, b: T): T
    override fun register() {
        AttributeSystem.operationManager.register(this)
    }
}