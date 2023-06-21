package com.skillw.attsystem.api.attribute

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.read.ReadPattern
import com.skillw.pouvoir.api.able.Registrable
import java.util.*

/**
 * Attribute
 *
 * @constructor Create Attribute
 * @property key 键
 * @property names 名称
 * @property readPattern 读取格式
 * @property priority 优先级
 */
class Attribute private constructor(
    override val key: String,
    val display: String,
    val names: List<String>,
    val readPattern: ReadPattern<*>,
    val priority: Int = 0,
) : Registrable<String>, Comparable<Attribute> {
    override fun compareTo(other: Attribute): Int = if (this.priority == other.priority) 0
    else if (this.priority > other.priority) 1
    else -1

    /** Entity */
    var entity = true

    /** Release */
    var release = false

    override fun register() {
        AttributeSystem.attributeManager.register(this)
    }

    var map: Map<String, Map<String, String>> = HashMap()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Attribute

        if (key != other.key) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + readPattern.hashCode()
        return result
    }

    override fun toString(): String {
        return "Attribute(key='$key')"
    }

    /**
     * Builder
     *
     * @param receiver
     * @constructor
     * @property key 键
     * @property readPattern 读取格式
     */
    class Builder(val key: String, private val readPattern: ReadPattern<*>, receiver: Builder.() -> Unit) {
        var display: String? = null

        /** Entity */
        var entity = true

        /** Release */
        var release = false

        /** Priority */
        var priority: Int = 0

        /** Names */
        val names = LinkedList<String>()

        var map: Map<String, Map<String, String>> = HashMap()

        init {
            receiver.invoke(this)
        }

        /**
         * Build
         *
         * @return
         */
        fun build(): Attribute {
            val att = Attribute(key, display ?: names.first, names, readPattern, priority)
            att.release = release
            att.entity = entity
            att.map = map
            return att
        }

    }

    companion object {
        @JvmStatic
        fun createAttribute(
            key: String,
            readPattern: ReadPattern<*>,
            init: Builder.() -> Unit,
        ): Attribute {
            return Builder(key, readPattern, init).build()
        }
    }
}
