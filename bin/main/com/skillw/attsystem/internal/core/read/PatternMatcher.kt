package com.skillw.attsystem.internal.core.read

import java.util.regex.Pattern

/**
 * @className `PatternMatcher'`
 *
 * @author Glom
 * @date 2022/8/7 22:55 Copyright 2022 user. All rights reserved.
 */
data class PatternMatcher<A>(val pattern: Pattern, val set: Set<Matcher<A>>)