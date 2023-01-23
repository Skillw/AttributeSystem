package com.skillw.attsystem.api.condition

import com.skillw.attsystem.AttributeSystem
import com.skillw.pouvoir.api.plugin.map.component.Registrable
import org.bukkit.entity.LivingEntity
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Base condition
 *
 * @param names 名称(含正则)
 * @constructor
 * @property key 键
 * @property type 类型
 */
abstract class BaseCondition(override val key: String, names: Set<String>, val type: ConditionType) :
    Registrable<String>, Condition {
    /** Patterns */
    val patterns = HashSet<Pattern>()

    init {
        names.forEach {
            patterns.add(it.toPattern())
        }
    }

    /** Release */
    var release = false

    /** Type line */
    fun typeLine() = type == ConditionType.LINE || type == ConditionType.ALL

    /** Type strings */
    fun typeStrings() = type == ConditionType.STRINGS || type == ConditionType.ALL

    /** Type all */
    fun typeAll() = type == ConditionType.ALL

    /**
     * Is type
     *
     * @param type
     */
    fun isType(type: ConditionType) = this.type == type || this.typeAll()

    /**
     * Condition type
     *
     * @constructor Create empty Condition type
     */
    enum class ConditionType {
        /**
         * Line
         *
         * 单行条件 (不符合则此行属性不生效)
         *
         * @constructor Create empty Line
         */
        LINE,

        /**
         * Strings
         *
         * 多行条件 (不符合则整个物品/字符串集合的属性不生效)
         *
         * @constructor Create empty Strings
         */
        STRINGS,

        /**
         * All
         *
         * 多行数据 兼 单行数据
         *
         * @constructor Create empty All
         */
        ALL
    }

    /**
     * Builder
     *
     * @constructor Create empty Builder
     * @property key 键
     * @property type 类型
     */
    class Builder(val key: String, val type: ConditionType) {
        /** Release */
        var release = false

        /** Names */
        val names = HashSet<String>()

        private val conditions = LinkedList<Condition>()

        /**
         * Condition
         *
         * @param condition
         */
        fun condition(
            condition: Condition,
        ) {
            conditions.add(condition)
        }

        /**
         * Build
         *
         * @return
         */
        fun build(): BaseCondition {
            return object : BaseCondition(key, names, type) {
                override fun condition(
                    slot: String?,
                    entity: LivingEntity?,
                    matcher: Matcher,
                    text: String,
                ): Boolean {
                    return conditions.all {
                        it.condition(slot, entity, matcher, text)
                    }
                }

                override fun conditionNBT(slot: String?, entity: LivingEntity?, map: Map<String, Any>): Boolean {
                    return conditions.all {
                        it.conditionNBT(slot, entity, map)
                    }
                }

                init {
                    this.release = this@Builder.release
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun createCondition(
            key: String,
            type: ConditionType,
            init: Builder.() -> Unit,
        ): BaseCondition {
            val builder = Builder(key, type)
            builder.init()
            return builder.build()
        }
    }

    override fun register() {
        AttributeSystem.conditionManager.register(this)
    }
}

