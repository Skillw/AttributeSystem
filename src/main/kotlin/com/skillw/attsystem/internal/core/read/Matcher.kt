package com.skillw.attsystem.internal.core.read

import com.skillw.attsystem.api.read.operation.Operation
import com.skillw.pouvoir.api.plugin.map.component.Keyable

/**
 * @className Matcher
 *
 * @author Glom
 * @date 2022/8/7 22:36 Copyright 2022 user. All rights reserved.
 */
class Matcher<A>(override val key: String, val operation: Operation<A>) : Keyable<String>, Operation<A> by operation