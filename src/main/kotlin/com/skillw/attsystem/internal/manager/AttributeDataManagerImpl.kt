package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.attributeDataManager
import com.skillw.attsystem.AttributeSystem.attributeSystemAPI
import com.skillw.attsystem.AttributeSystem.configManager
import com.skillw.attsystem.AttributeSystem.equipmentDataManager
import com.skillw.attsystem.api.AttrAPI.read
import com.skillw.attsystem.api.AttrAPI.updateAttr
import com.skillw.attsystem.api.attribute.compound.AttributeData
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.attsystem.api.event.AttributeUpdateEvent
import com.skillw.attsystem.api.manager.AttributeDataManager
import com.skillw.attsystem.internal.manager.PersonalManagerImpl.pullInitialAttrData
import com.skillw.pouvoir.util.EntityUtils.isAlive
import com.skillw.pouvoir.util.EntityUtils.livingEntity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import taboolib.common.platform.service.PlatformExecutor
import taboolib.platform.util.isNotAir
import java.util.*

object AttributeDataManagerImpl : AttributeDataManager() {
    override val key = "AttributeDataManager"
    override val priority: Int = 3
    override val subPouvoir = AttributeSystem

    private var clearTask:
            PlatformExecutor.PlatformTask? = null

    private fun clearTask() {
        clearTask?.cancel()
        clearTask = submit(period = configManager.attributeClearSchedule) {
            attributeDataManager.keys.forEach {
                val livingEntity = it.livingEntity()
                if (livingEntity?.isValid != true || it.livingEntity()?.isDead != false) {
                    attributeSystemAPI.remove(it)
                }
            }
        }
    }

    override fun onActive() {
        onReload()
    }

    override var playerBaseAttribute: AttributeData = AttributeData()
    override var entityBaseAttribute: AttributeData = AttributeData()

    override fun onReload() {
        playerBaseAttribute =
            configManager["config"].getStringList("options.attribute.base-attribute.player").read()
        entityBaseAttribute =
            configManager["config"].getStringList("options.attribute.base-attribute.entity").read()
        clearTask()
    }

    override fun get(key: UUID): AttributeDataCompound? {
        return super.get(key) ?: kotlin.run { key.livingEntity()?.updateAttr(); super.get(key) } ?: pullInitialAttrData(
            key
        )?.compound
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
                        equipmentDataManager.readItem(itemStack, entity, equipmentKey)
                    )
            }
        }
        attrData.operation(equipAtt)

        attrData.register(
            "BASE-ATTRIBUTE",
            if (entity is Player) attributeDataManager.playerBaseAttribute else attributeDataManager.entityBaseAttribute
        )
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
        key: String,
        attributes: Collection<String>,

        ): AttributeData {
        return this.addAttrData(entity.uniqueId, key, attributes)
    }

    override fun addAttrData(
        entity: LivingEntity,
        key: String,
        attributeData: AttributeData,

        ): AttributeData {
        if (!entity.isAlive()) {
            return attributeData
        }
        val uuid = entity.uniqueId
        if (attributeDataManager.containsKey(uuid)) {
            attributeDataManager[uuid]!!.register(key, attributeData)
        } else {
            val compound = AttributeDataCompound()
            compound.register(key, attributeData)
            attributeDataManager.register(uuid, compound)
        }
        return attributeData
    }

    override fun addAttrData(
        uuid: UUID,
        key: String,
        attributes: Collection<String>,

        ): AttributeData {
        return this.addAttrData(
            uuid,
            key,
            attributeSystemAPI.read(attributes, uuid.livingEntity())
        )
    }

    override fun addAttrData(uuid: UUID, key: String, attributeData: AttributeData): AttributeData {
        return uuid.livingEntity()?.let { addAttrData(it, key, attributeData) } ?: AttributeData()
    }

    override fun removeAttrData(entity: LivingEntity, key: String) {
        if (!entity.isAlive()) return
        attributeDataManager[entity.uniqueId]?.run {
            remove(key)
        }
    }

    override fun removeAttrData(uuid: UUID, key: String) {
        uuid.livingEntity()?.let { removeAttrData(it, key) }
    }


    override fun put(key: UUID, value: AttributeDataCompound): AttributeDataCompound? {
        return super.put(key, value)?.apply { entity = key.livingEntity() }
    }

}
