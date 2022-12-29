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

    private var task:
            PlatformExecutor.PlatformTask? = null

    private fun clearTask() {
        task?.cancel()
        task = submit(period = configManager.attributeClearSchedule) {
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
        val entity = key.livingEntity() ?: return null
        return super.get(key) ?: kotlin.run { entity.updateAttr(); super.get(key) }
    }

    override fun update(entity: LivingEntity): AttributeDataCompound? {
        if (!entity.isAlive()) return null
        val uuid = entity.uniqueId
        val equipData = equipmentDataManager[uuid] ?: return null
        var attrData =
            if (attributeDataManager.containsKey(uuid)) attributeDataManager[uuid]!!.clone()
            else AttributeDataCompound(entity)
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
                        equipmentDataManager.readItem(itemStack, entity, equipmentKey)
                    )
            }
        }
        attrData.operation(equipAtt)

        attrData.register(
            "BASE-ATTRIBUTE",
            if (entity is Player) attributeDataManager.playerBaseAttribute else attributeDataManager.entityBaseAttribute
        )
        attrData.init()

        //AFTER
        val afterEvent =
            AttributeUpdateEvent.After(entity, attrData)
        afterEvent.call()
        attrData = afterEvent.compound
        attributeDataManager.register(uuid, attrData)
        return attrData
    }

    override fun addAttribute(
        entity: LivingEntity,
        key: String,
        attributes: Collection<String>,
        release: Boolean,
    ): AttributeData {
        return this.addAttribute(entity.uniqueId, key, attributes, release)
    }

    override fun addAttribute(
        entity: LivingEntity,
        key: String,
        attributeData: AttributeData,
        release: Boolean,
    ): AttributeData {
        return this.addAttribute(entity.uniqueId, key, attributeData, release)
    }

    override fun addAttribute(
        uuid: UUID,
        key: String,
        attributes: Collection<String>,
        release: Boolean,
    ): AttributeData {
        return this.addAttribute(
            uuid,
            key,
            attributeSystemAPI.read(attributes, uuid.livingEntity()),
            release
        )
    }

    override fun addAttribute(uuid: UUID, key: String, attributeData: AttributeData, release: Boolean): AttributeData {
        if (!uuid.isAlive()) {
            return attributeData
        }
        attributeData.release = release
        if (attributeDataManager.containsKey(uuid)) {
            attributeDataManager[uuid]!!.register(key, attributeData)
        } else {
            val compound = AttributeDataCompound()
            compound.register(key, attributeData)
            attributeDataManager.register(uuid, compound)
        }
        return attributeData
    }

    override fun removeAttribute(entity: LivingEntity, key: String) {
        removeAttribute(entity.uniqueId, key)
    }

    override fun removeAttribute(uuid: UUID, key: String) {
        if (!uuid.isAlive()) return
        if (attributeDataManager.containsKey(uuid)) {
            attributeDataManager[uuid]!!.remove(key)
        }
    }

    override fun put(key: UUID, value: AttributeDataCompound): AttributeDataCompound? {
        return super.put(key, value)?.apply { entity = key.livingEntity() }
    }

}
