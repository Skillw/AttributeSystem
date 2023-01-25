package com.skillw.attsystem.internal.feature.realizer

import com.skillw.asahi.api.AsahiAPI.analysis
import com.skillw.asahi.api.member.context.AsahiContext
import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.AttrAPI.addAttribute
import com.skillw.attsystem.api.AttrAPI.read
import com.skillw.attsystem.api.AttrAPI.readAttData
import com.skillw.attsystem.api.AttrAPI.removeAttribute
import com.skillw.attsystem.api.attribute.compound.AttributeData
import com.skillw.attsystem.api.realizer.BaseRealizer
import com.skillw.attsystem.api.realizer.component.sub.Realizable
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.util.asList
import taboolib.module.configuration.util.asMap

/**
 * @className BaseAttributeRealizer
 *
 * @author Glom
 * @date 2023/1/6 7:05 Copyright 2022 user. All rights reserved.
 */
@AutoRegister
internal object BaseAttributeRealizer : BaseRealizer("base-attribute"), Realizable {

    override val file by lazy {
        AttributeSystem.options.file!!
    }
    val type
        get() = config["type"]?.toString()?.lowercase() ?: "strings"
    val values
        get() = config["values"]

    @Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
    private fun <T> T.analysisAny(
        entity: LivingEntity,
        context: AsahiContext = AsahiContext.create()
            .apply { put("entity", entity); if (entity is Player) put("player", entity) },
    ): T {

        return when (this) {
            is MutableMap<*, *> -> mapKeys {
                it.key.toString().analysisAny(entity, context)
            }.mapValues {
                it.value.analysisAny(entity, context)
            }

            is List<*> -> map { it.analysisAny(entity, context) }

            is String -> analysis(context)
            else -> this
        } as T
    }

    private const val dataKey = "BASE-ATTRIBUTE"

    override fun realize(entity: LivingEntity) {
        val data = when (type) {
            "nbt" -> values.asMap().entries.associate { it.key to it.value!! }
                .analysisAny(entity).readAttData()

            else -> values?.asList()?.read(entity) ?: AttributeData()
        }
        entity.addAttribute(dataKey, data)
    }

    override fun unrealize(entity: LivingEntity) {
        entity.removeAttribute(dataKey)
    }


}