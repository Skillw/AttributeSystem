package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.attributeDataManager
import com.skillw.attsystem.AttributeSystem.attributeSystemAPI
import com.skillw.attsystem.AttributeSystem.equipmentDataManager
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
            AttributeUpdateEvent.Post(entity, attrData)
        preEvent.call()
        attrData = preEvent.compound
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

        this[uuid] = attrData
        attrData.init()

        //AFTER
        val afterEvent =
            AttributeUpdateEvent.After(entity, attrData)
        afterEvent.call()
        attrData = afterEvent.compound
        this[uuid] = attrData
        return attrData
    }

    override fun addAttribute(
        entity: LivingEntity,
        key: String,
        attributes: Collection<String>,
        release: Boolean,
    ): AttributeData {
        return this.addAttribute(
            entity,
            key,
            attributeSystemAPI.read(attributes, entity),
            release
        )
    }

    override fun addAttribute(
        entity: LivingEntity,
        key: String,
        attributeData: AttributeData,
        release: Boolean,
    ): AttributeData {
        return this.addAttribute(entity.uniqueId, key, attributeData, release)
    }

    override fun addAttribute(uuid: UUID, key: String, attributeData: AttributeData, release: Boolean): AttributeData {
        attributeData.release = release
        getOrPut(uuid) { AttributeDataCompound() }.register(key, attributeData)

        return attributeData
    }

    override fun removeAttribute(entity: LivingEntity, key: String) {
        removeAttribute(entity.uniqueId, key)
    }

    override fun removeAttribute(uuid: UUID, key: String) {
        attributeDataManager[uuid]?.remove(key)
    }

    override fun put(key: UUID, value: AttributeDataCompound): AttributeDataCompound? {
        return super.put(key, value)?.apply { entity = key.livingEntity() }
    }

}
