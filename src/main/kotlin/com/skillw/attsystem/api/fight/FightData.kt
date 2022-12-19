package com.skillw.attsystem.api.fight

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.AttrAPI.getAttrData
import com.skillw.attsystem.api.AttrAPI.hasData
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.attsystem.api.event.FightDataHandleEvent
import com.skillw.attsystem.api.fight.message.MessageData
import com.skillw.attsystem.api.operation.OperationElement
import com.skillw.attsystem.internal.feature.compat.pouvoir.AttributePlaceHolder
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.pouvoir.Pouvoir
import com.skillw.pouvoir.api.PouvoirAPI.analysis
import com.skillw.pouvoir.api.PouvoirAPI.eval
import com.skillw.pouvoir.api.function.context.IContext
import com.skillw.pouvoir.internal.core.function.context.SimpleContext
import com.skillw.pouvoir.util.ColorUtils.decolored
import com.skillw.pouvoir.util.MessageUtils.info
import com.skillw.pouvoir.util.StringUtils.parse
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.util.asList
import taboolib.common5.Coerce
import taboolib.module.chat.uncolored
import taboolib.module.nms.getI18nName
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer

/**
 * Fight data
 *
 * @constructor Create empty Fight data
 * @property attacker 攻击者
 * @property defender 防御者
 */
class FightData(attacker: LivingEntity?, defender: LivingEntity?) : IContext by SimpleContext(ConcurrentHashMap()) {


    constructor(attacker: LivingEntity?, defender: LivingEntity?, run: Consumer<FightData>) : this(
        attacker,
        defender
    ) {
        run.accept(this)
    }

    /** Attacker */
    var attacker: LivingEntity? = null
        set(value) {
            if (attackerData == null) {
                attackerData = attacker?.getAttrData() ?: AttributeDataCompound()
            }
            field = value
            if (value != null) {
                this["attacker-name"] =
                    (value as? Player)?.displayName
                        ?: (if (value.customName == null) value.getI18nName() else value.customName)
                                ?: ASConfig.defaultAttackerName
            }
        }

    /** Attacker data */
    private var attackerData: AttributeDataCompound? = null


    /** Defender */
    var defender: LivingEntity? = null
        set(value) {
            if (defenderData == null) defenderData = defender?.getAttrData() ?: AttributeDataCompound()
            field = value
            if (value != null) {
                this["defender-name"] =
                    (value as? Player)?.displayName
                        ?: (if (value.customName == null) value.getI18nName() else value.customName)
                                ?: ASConfig.defaultDefenderName
            }
        }
    private var defenderData: AttributeDataCompound? = null

    init {
        this.attacker = attacker
        this.defender = defender
        if (attacker != null && !attacker.hasData())
            AttributeSystem.attributeSystemAPI.update(attacker)
        if (defender != null && !defender.hasData())
            AttributeSystem.attributeSystemAPI.update(defender)
        attacker?.let { this["attacker"] = it }
        defender?.let { this["defender"] = it }
    }

    /** MessageType data */
    val messageData = MessageData()

    /** Damage sources */
    val damageSources = LinkedHashMap<String, OperationElement>()


    /** Has result */
    var hasResult = true

    /** Cal message */
    var calMessage = true

    /**
     * Cal result
     *
     * @return result
     */
    fun calResult(): Double {
        if (!hasResult) return 0.0
        var result = 0.0
        damageSources.values.forEach {
            result = it.operate(result).toDouble()
        }
        return result
    }


    constructor(fightData: FightData) : this(fightData.attacker, fightData.defender) {
        putAll(fightData)
    }


    /**
     * Handle map
     *
     * @param map
     * @param K
     * @param V
     * @return
     */
    fun handleMap(map: Map<*, *>, log: Boolean = true): Map<String, Any> {
        val newMap = ConcurrentHashMap<String, Any>()
        map.forEach { (key, value) ->
            if (log)
                ASConfig.debug { info("      &e$key&5:") }
            newMap[key.toString()] = handle(value ?: return@forEach, log)
        }
        return newMap
    }

    /**
     * 解析Any
     *
     * 给脚本用的
     *
     * @param any 字符串/字符串集合/Map
     * @return 解析后的Any
     */
    fun handle(any: Any): Any {
        return handle(any, true)
    }

    /**
     * 解析Any
     *
     * @param any 字符串/字符串集合/Map
     * @return 解析后的Any
     */
    fun handle(any: Any, log: Boolean = true): Any {
        if (any is String) {
            return handleStr(any, log)
        }
        if (any is List<*>) {
            if (any.isEmpty()) return "[]"
            if (any[0] is Map<*, *>) {
                val mapList = Coerce.toListOf(any, Map::class.java)
                val newList = LinkedList<Map<*, *>>()
                mapList.forEach {
                    newList.add(handleMap(it))
                }
                return newList
            }
            return handleList(any.asList(), log)
        }
        if (any is Map<*, *>) {
            return handleMap(any)
        }
        return any
    }

    private fun String.attValue(entity: LivingEntity, data: AttributeDataCompound): String {
        val placeholder = substring(3)
        return AttributePlaceHolder.placeholder(placeholder, entity, data)
    }

    private fun String.placeholder(
        str: String,
        entity: LivingEntity,
        data: AttributeDataCompound,
        log: Boolean = true,
    ): String {
        val placeholder = str.substring(2)
        val value = if (placeholder.startsWith("as.")) placeholder.attValue(
            entity,
            data
        ) else Pouvoir.pouPlaceHolderAPI.replace(entity, "%${placeholder}%")
        if (log)
            ASConfig.debug {
                info(
                    "       &3{${str.uncolored().decolored()}} &7-> &9${
                        value.uncolored().decolored()
                    }"
                )
            }
        return replace(
            "{$str}",
            value
        )
    }

    /**
     * Handle
     *
     * @param string 待解析字符串
     * @return 解析后的字符串
     */
    fun handleStr(string: String, log: Boolean = true): String {
        val event = FightDataHandleEvent(this, string)
        event.call()
        var formula = event.string
        val list = formula.parse('{', '}')
        for (str in list) {
            when {
                attacker != null && str.startsWith("a.") -> {
                    formula = formula.placeholder(str, attacker!!, attackerData!!, log)
                    continue
                }

                defender != null && str.startsWith("d.") -> {
                    formula = formula.placeholder(str, defender!!, defenderData!!, log)
                }

                else -> {
                    val replacement = this[str] ?: continue
                    formula = formula.replace("{$str}", replacement.toString())
                    if (log)
                        ASConfig.debug {
                            info(
                                "       &3{${str.uncolored().decolored()}} &7-> &9${
                                    replacement.toString().uncolored().decolored()
                                }}"
                            )
                        }
                    continue
                }
            }
        }
        val value = formula.eval(namespaces = arrayOf("common", "attsystem"), context = this).toString().analysis(this)

        if (log) ASConfig.debug {
            info(
                "      &3${formula.uncolored().decolored()} &7-> &9${
                    value.uncolored().decolored()
                }"
            )
        }
        return value
    }

    /**
     * 解析
     *
     * @param strings 待解析的字符串集
     * @return 解析后的字符串集
     */
    fun handleList(strings: Collection<String>, log: Boolean = true): List<String> {
        val list = LinkedList<String>()
        strings.forEach {
            list.add(handleStr(it, log))
        }
        return list
    }
}