package com.skillw.attsystem.internal.core.read

/**
 * @className `PatternMatcher'`
 *
 * @author Glom
 * @date 2022/8/7 22:55 Copyright 2022 user. All rights reserved.
 */
data class StringMatcher<A>(val string: String, val set: Set<Matcher<A>>)