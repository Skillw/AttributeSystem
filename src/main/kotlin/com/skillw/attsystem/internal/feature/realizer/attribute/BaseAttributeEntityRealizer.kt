package com.skillw.attsystem.internal.feature.realizer.attribute

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.AttrAPI.read
import com.skillw.attsystem.api.AttrAPI.update
import com.skillw.attsystem.api.compiled.CompiledAttrDataCompound
import com.skillw.attsystem.api.compiled.sub.ComplexCompiledData
import com.skillw.pouvoir.api.feature.realizer.BaseRealizer
import com.skillw.pouvoir.api.feature.realizer.BaseRealizerManager
import com.skillw.pouvoir.api.feature.realizer.component.Awakeable
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister
import com.skillw.pouvoir.util.isAlive
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntitySpawnEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.util.asList
import taboolib.common.util.unsafeLazy
import taboolib.common5.cbool
import taboolib.module.configuration.util.asMap

/**
 * @className BaseAttributePlayerRealizer
 *
 * Ӧ�ý��� basic attribute�ġ���
 *
 * @author Glom
 * @date 2023/1/6 7:05 Copyright 2022 user. All rights reserved.
 */
@AutoRegister
object BaseAttributeEntityRealizer : BaseRealizer("base-attribute-entity"), Awakeable {
    override val file by lazy {
        AttributeSystem.options.file!!
    }
    override val manager: BaseRealizerManager by unsafeLazy {
        AttributeSystem.realizerManager
    }

    val type
        get() = config["type"]?.toString()?.lowercase() ?: "strings"
    val attrData
        get() = config["attributes"]
    val conditions
        get() = config["conditions"]
    val onSpawn
        get() = config["on-spawn"]?.cbool ?: true

    private const val KEY = "BASIC-ATTRIBUTE"

    var baseData: ComplexCompiledData = ComplexCompiledData()


    override fun onEnable() {
        onReload()
    }

    override fun onActive() {
        onReload()
    }

    override fun onReload() {
        val base = when (type) {
            "nbt" -> {
                val attrData = attrData.asMap().entries.associate { it.key to it.value!! }.toMutableMap()
                val conditions = conditions as? List<Any>? ?: emptyList()
                AttributeSystem.readManager.readMap(attrData, conditions)
            }

            else -> attrData?.asList()?.read()
        }
        baseData.base = base
    }

    fun CompiledAttrDataCompound.baseEntity(): CompiledAttrDataCompound {
        this[KEY] = baseData
        return this
    }

    @SubscribeEvent
    fun entity(event: EntitySpawnEvent) {
        if(!onSpawn) return
        val entity =  event.entity
        if(entity.isAlive())
            (entity as LivingEntity).update()
    }


}