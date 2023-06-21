package com.skillw.attsystem.internal.core.read

import com.skillw.attsystem.api.operation.Operation
import com.skillw.pouvoir.api.able.Keyable

/**
 * @className Matcher
 *
 * @author Glom
 * @date 2022/8/7 22:36 Copyright 2022 user. All rights reserved.
 */
class Matcher<A>(override val key: String, val operation: Operation<A>) : Keyable<String>