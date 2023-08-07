package com.skillw.attsystem.internal.core.read.str

import com.skillw.attsystem.api.attribute.Attribute
import com.skillw.attsystem.api.read.status.Status
import com.skillw.attsystem.api.read.status.StringStatus
import com.skillw.attsystem.internal.core.read.BaseReadGroup
import org.bukkit.entity.LivingEntity

/**
 * String reader
 *
 * 是AttributeSystem默认的读取格式实现，用于读取字符串属性
 *
 * @param key
 * @param matchers 捕获组
 * @param patternStrings 正则表达式
 * @param placeholders 占位符
 * @constructor
 */
class StringReader(
    key: String,
    matchers: Map<String, String>,
    patternStrings: List<String>,
    placeholders: Map<String, String>,
) : BaseReadGroup<String>(key, matchers, patternStrings, placeholders) {

    override fun read(string: String, attribute: Attribute, entity: LivingEntity?, slot: String?): StringStatus? {
        if ((attribute.names.none { string.contains(it) })) return null
        val attributeStatus = StringStatus(this)
        var temp = string
        attribute.names.forEach {
            if (temp.contains(it)) temp = temp.replaceFirst(it, "{name}")
        }
        patternList@ for ((pattern, matchers) in patterns) {
            val matcher = pattern.matcher(temp)
            if (!matcher.find()) continue
            matchers.forEach { nMatcher ->
                val key = nMatcher.key
                val valueStr = matcher.group(key)
                attributeStatus.operation(key, valueStr, nMatcher.operation)
            }
            break@patternList
        }
        return attributeStatus
    }

    override fun readNBT(map: Map<String, Any>, attribute: Attribute): StringStatus {
        return StringStatus(this).apply {
            putAll(map.mapValues { it.value.toString() })
        }
    }

    override fun onPlaceholder(
        key: String,
        attribute: Attribute,
        status: Status<String>,
        entity: LivingEntity?,
    ): String? {
        return replacePlaceholder(key, status, entity)
    }

}