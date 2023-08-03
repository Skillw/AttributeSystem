package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.attributeDataManager
import com.skillw.attsystem.AttributeSystem.compiledAttrDataManager
import com.skillw.attsystem.api.AttrAPI.update
import com.skillw.attsystem.api.attribute.compound.AttributeData
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.attsystem.api.event.AttributeUpdateEvent
import com.skillw.attsystem.api.manager.AttributeDataManager
import com.skillw.attsystem.internal.manager.PersonalManagerImpl.pullInitialAttrData
import com.skillw.attsystem.util.Utils.validEntity
import com.skillw.pouvoir.util.EntityUtils.isAlive
import com.skillw.pouvoir.util.EntityUtils.livingEntity
import org.bukkit.entity.LivingEntity
import java.util.*

object AttributeDataManagerImpl : AttributeDataManager() {
    override val key = "AttributeDataManager"
    override val priority: Int = 3
    override val subPouvoir = AttributeSystem

    override fun onActive() {
        onReload()
    }

    override fun get(key: UUID): AttributeDataCompound? {
        return super.get(key) ?: kotlin.run { key.validEntity().update(); super.get(key) } ?: pullInitialAttrData(
            key
        )?.compound
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
            //第一次，无条件属性
            attrData.combine(eval(entity))
            attributeDataManager[uuid] = attrData
            //第二次，第一层 条件属性
            attrData.putAll(eval(entity))
            attributeDataManager[uuid] = attrData
            //第三次，第二层 条件属性
            attrData.putAll(eval(entity))
        }

        val process =
            AttributeUpdateEvent.Process(entity, attrData)
        process.call()
        attrData = process.data
        this[uuid] = attrData
        attrData.init()

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
