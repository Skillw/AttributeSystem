package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.attributeDataManager
import com.skillw.attsystem.AttributeSystem.equipmentDataManager
import com.skillw.attsystem.AttributeSystem.readManager
import com.skillw.attsystem.api.AttrAPI.readItem
import com.skillw.attsystem.api.AttrAPI.updateAttr
import com.skillw.attsystem.api.attribute.compound.AttributeData
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.attsystem.api.event.AttributeUpdateEvent
import com.skillw.attsystem.api.manager.AttributeDataManager
import com.skillw.pouvoir.util.isAlive
import com.skillw.pouvoir.util.livingEntity
import org.bukkit.entity.LivingEntity
import taboolib.platform.util.isNotAir
import java.util.*

object AttributeDataManagerImpl : AttributeDataManager() {
    override val key = "AttributeDataManager"
    override val priority: Int = 3
    override val subPouvoir = AttributeSystem

    override fun get(key: UUID): AttributeDataCompound? {
        return super.get(key) ?: kotlin.run { key.livingEntity()?.updateAttr(); super.get(key) }
    }

    override fun update(entity: LivingEntity): AttributeDataCompound? {
        if (!entity.isAlive()) return null
        val uuid = entity.uniqueId
        val equipData = equipmentDataManager[uuid] ?: return null
        var attrData =
            if (attributeDataManager.containsKey(uuid)) attributeDataManager[uuid]!!.clone()
            else AttributeDataCompound(entity).also { this[uuid] = it }
        //PRE
        val preEvent =
            AttributeUpdateEvent.Pre(entity, attrData)
        preEvent.call()
        attrData = preEvent.data
        attrData.release()
        //PROCESS
        val equipAtt = AttributeDataCompound(entity)
        equipData.forEach { (_, equip) ->
            now@ for ((equipmentKey, itemStack) in equip) {
                if (itemStack.isNotAir())
                    equipAtt.operation(
                        itemStack.readItem(entity, equipmentKey)
                    )
            }
        }
        attrData.operation(equipAtt)

        val process = AttributeUpdateEvent.Process(entity, attrData)
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
        key: String,
        attributes: Collection<String>,
    ): AttributeData {
        return this.addAttrData(
            entity,
            key,
            readManager.read(attributes, entity)
        )
    }

    override fun addAttrData(
        entity: LivingEntity,
        key: String,
        attributeData: AttributeData,

        ): AttributeData {
        return this.addAttrData(entity.uniqueId, key, attributeData)
    }

    override fun addAttrData(uuid: UUID, key: String, attributes: Collection<String>): AttributeData? {
        return addAttrData(uuid, key, attributes)
    }

    override fun addAttrData(uuid: UUID, key: String, attributeData: AttributeData): AttributeData {
        map.computeIfAbsent(uuid) { AttributeDataCompound() }.register(key, attributeData)
        return attributeData
    }

    override fun removeAttrData(entity: LivingEntity, key: String) {
        removeAttrData(entity.uniqueId, key)
    }

    override fun removeAttrData(uuid: UUID, key: String) {
        attributeDataManager[uuid]?.remove(key)
    }

    override fun put(key: UUID, value: AttributeDataCompound): AttributeDataCompound? {
        return super.put(key, value)?.apply { entity = key.livingEntity() }
    }

}
