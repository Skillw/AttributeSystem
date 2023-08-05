package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.attributeDataManager
import com.skillw.attsystem.AttributeSystem.compileManager
import com.skillw.attsystem.AttributeSystem.compiledAttrDataManager
import com.skillw.attsystem.api.attribute.compound.AttributeData
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.attsystem.api.event.AttributeUpdateEvent
import com.skillw.attsystem.api.manager.AttributeDataManager
import com.skillw.attsystem.internal.feature.personal.InitialAttrData.Companion.pullAttrData
import com.skillw.pouvoir.util.isAlive
import com.skillw.pouvoir.util.livingEntity
import org.bukkit.entity.LivingEntity
import java.util.*

object AttributeDataManagerImpl : AttributeDataManager() {
    override val key = "AttributeDataManager"
    override val priority: Int = 3
    override val subPouvoir = AttributeSystem

    override fun get(key: UUID): AttributeDataCompound? {
        return uncheckedGet(key) ?: pullAttrData(key)?.compound
    }

    private fun uncheckedGet(key: UUID): AttributeDataCompound? {
        return super.get(key)
    }

    override fun update(entity: LivingEntity): AttributeDataCompound? {
        if (!entity.isAlive()) return null
        val uuid = entity.uniqueId
        var attrData =
            uncheckedGet(uuid)?.clone() ?: AttributeDataCompound(entity).also { this[uuid] = it }
        //PRE
        val preEvent =
            AttributeUpdateEvent.Pre(entity, attrData)
        preEvent.call()
        attrData = preEvent.data
        attrData.release()
        //PROCESS

        compiledAttrDataManager[uuid].apply {
            attrData.combine(eval(entity))
        }

        val process =
            AttributeUpdateEvent.Process(entity, attrData)
        process.call()
        attrData = process.data
        this[uuid] = attrData
        attrData.init()

        attrData.combine(compileManager.mapping(entity)(attrData.toAttributeData()).eval(entity).allToRelease())

        //AFTER
        val postEvent =
            AttributeUpdateEvent.Post(entity, attrData)
        postEvent.call()
        attrData = postEvent.data
        this[uuid] = attrData
        return attrData
    }

    override fun addAttrData(
        entity: LivingEntity,
        source: String,
        attributeData: AttributeData,

        ): AttributeData {
        if (!entity.isAlive()) {
            return attributeData
        }
        val uuid = entity.uniqueId
        if (attributeDataManager.containsKey(uuid)) {
            attributeDataManager[uuid]!!.register(source, attributeData)
        } else {
            val compound = AttributeDataCompound()
            compound.register(source, attributeData)
            attributeDataManager.register(uuid, compound)
        }
        return attributeData
    }

    override fun addAttrData(uuid: UUID, source: String, attributeData: AttributeData): AttributeData {
        return uuid.livingEntity()?.let { addAttrData(it, source, attributeData) } ?: AttributeData()
    }

    override fun removeAttrData(entity: LivingEntity, source: String): AttributeData? {
        if (!entity.isAlive()) return null
        return attributeDataManager[entity.uniqueId]?.run {
            remove(source)
        }
    }

    override fun removeAttrData(uuid: UUID, source: String): AttributeData? {
        return uuid.livingEntity()?.let { removeAttrData(it, source) }
    }


    override fun put(key: UUID, value: AttributeDataCompound): AttributeDataCompound? {
        return super.put(key, value)?.apply { entity = key.livingEntity() }
    }

}
