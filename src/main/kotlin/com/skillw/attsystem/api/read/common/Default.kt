package com.skillw.attsystem.api.read.common

import com.skillw.attsystem.internal.core.read.num.NumberReader
import taboolib.common.util.unsafeLazy

/**
 * @className Default
 *
 * @author Glom
 * @date 2023/8/6 14:27 Copyright 2023 user. All rights reserved.
 */

private val defaultValueReader by unsafeLazy {
    NumberReader(
        "default", mapOf(
            "percentMax" to "plus",
            "percentMin" to "plus",
            "valueMin" to "plus",
            "valueMax" to "plus",
            "percent" to "plus",
            "value" to "plus",
            "scalar" to "scalar"
        ), listOf(
            "{name}(:|£º)?\\s?<percentMin>-<percentMax>\\(%\\)",
            "{name}(:|£º)?\\s?<valueMin>-<valueMax>",
            "{name}(:|£º)?\\s?<percent>\\(%\\)",
            "{name}(:|£º)?\\s?<value>",
            "{name}\\*<scalar>"
        ),
        mapOf(
            "total" to "( <value> + {random <valueMin> to <valueMax>})*(1+(<percent>/100 + {random <percentMin> to <percentMax>} /100)) * { if check <scalar> == 0 then 1 else <scalar> }",
            "scalar" to "<scalar>",
            "value" to "<value>",
            "percent" to "<percent>/100",
            "valueMin" to "<valueMin>",
            "valueMax" to "<valueMax>",
            "percentMin" to "<percentMin>/100",
            "percentMax" to "<percentMax>/100",
            "valueRandom" to "<value> + {random <valueMin> to <valueMax>}",
            "percentRandom" to "(<percent>/100 + {random <percentMin> to <percentMax>}/100)"
        )
    )
}
private val defaultPercentReader by unsafeLazy {
    NumberReader(
        "percent", mapOf(
            "percentMax" to "plus",
            "percentMin" to "plus",
            "percent" to "plus"
        ), listOf(
            "{name}(:|£º)?\\s?<percentMin>-<percentMax>\\(%\\)",
            "{name}(:|£º)?\\s?<percent>\\(%\\)",
        ),
        mapOf(
            "total" to "(<percent>/100 + {random <percentMin> to <percentMax> }/100)",
            "percent" to "<percent>/100",
            "percentMin" to "<percentMin>/100",
            "percentMax" to "<percentMax>/100",
            "percentRandom" to "(<percent>/100 + {random <percentMin> to <percentMax>}/100)"
        )
    )
}