package com.skillw.attsystem.api.event

import com.skillw.attsystem.api.compiled.CompiledData
import org.bukkit.entity.LivingEntity
import taboolib.platform.type.BukkitProxyEvent

/**
 * 读取字符串属性事件
 *
 * @constructor Create empty Strings read event
 * @property entity 实体
 * @property strings 字符串集
 * @property compiledData 预编译属性数据
 */
open class StringsReadEvent(
    val entity: LivingEntity?,
    val strings: Collection<String>,
    val compiledData: CompiledData,
) : BukkitProxyEvent()