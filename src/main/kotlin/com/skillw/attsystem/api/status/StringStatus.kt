package com.skillw.attsystem.api.status

import com.skillw.attsystem.internal.core.read.ReadGroup

/**
 * Number status
 *
 * @constructor Create empty Number status
 * @property numberReader
 */
class StringStatus(numberReader: ReadGroup<String>) : GroupStatus<String>(numberReader) {
    override fun clone(): StringStatus {
        val status = StringStatus(readGroup)
        this.forEach {
            status.register(it.key, it.value)
        }
        return status
    }
}
