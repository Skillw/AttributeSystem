package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.equipmentDataManager
import com.skillw.attsystem.api.AttrAPI.updateAttr
import com.skillw.attsystem.api.attribute.compound.AttributeData
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.attsystem.api.equipment.EquipmentData
import com.skillw.attsystem.api.equipment.EquipmentDataCompound
import com.skillw.attsystem.api.event.EquipmentUpdateEvent
import com.skillw.attsystem.api.event.ItemLoadEvent
import com.skillw.attsystem.api.event.ItemReadEvent
import com.skillw.attsystem.api.manager.EquipmentDataManager
import com.skillw.pouvoir.api.map.BaseMap
import com.skillw.pouvoir.util.EntityUtils.isAlive
import com.skillw.pouvoir.util.EntityUtils.livingEntity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common5.Coerce
import taboolib.common5.mirrorNow
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.module.chat.uncolored
import taboolib.module.nms.*
import taboolib.platform.util.hasLore
import taboolib.platform.util.isAir
import taboolib.platform.util.isNotAir
import java.util.*

object EquipmentDataManagerImpl : EquipmentDataManager() {
    override val key = "EquipmentDataManager"
    override val priority: Int = 4
    override val subPouvoir = AttributeSystem


    override fun get(key: UUID): EquipmentDataCompound? {
        return super.get(key) ?: kotlin.run { key.livingEntity()?.updateAttr(); super.get(key) }
    }

    override fun update(entity: LivingEntity): EquipmentDataCompound? {
        if (!entity.isAlive()) return null
        val uuid = entity.uniqueId
        var dataCompound =
            if (containsKey(uuid)) EquipmentDataCompound(this.map[uuid]!!) else EquipmentDataCompound()
        val pre = EquipmentUpdateEvent.Pre(entity, dataCompound)
        pre.call()
        if (pre.isCancelled) {
            return dataCompound
        }
        dataCompound = pre.data
        equipmentDataManager.register(uuid, dataCompound)
        dataCompound.remove("BASE-EQUIPMENT")
        if (entity is Player) {
            loadPlayer(entity, dataCompound)
        } else {
            loadLivingEntity(entity, dataCompound)
        }
        val postEvent = EquipmentUpdateEvent.Post(entity, dataCompound)
        postEvent.call()
        if (postEvent.isCancelled) {
            return dataCompound
        }
        dataCompound = postEvent.data
        equipmentDataManager.register(uuid, dataCompound)
        return dataCompound
    }

    private fun loadLivingEntity(entity: LivingEntity, dataCompound: EquipmentDataCompound) {
        for ((key, equipmentType) in AttributeSystem.entitySlotManager) {
            val origin: ItemStack? = equipmentType.getItem(entity)
            if (origin == null || origin.isAir()) {
                continue
            }
            val event = ItemLoadEvent(entity, origin)
            val eventItem = event.itemStack
            event.call()
            if (event.isCancelled) {
                return
            }
            if (eventItem.isSimilar(origin)) {
                dataCompound["BASE-EQUIPMENT", key] = eventItem
                return
            }
            if (eventItem.isNotAir())
                dataCompound["BASE-EQUIPMENT", key] = eventItem
        }
    }

    private fun loadPlayer(player: Player, dataCompound: EquipmentDataCompound) {
        val inv = player.inventory
        val manager = AttributeSystem.playerSlotManager
        for (playerSlot in manager.values) {
            val equipmentType = playerSlot.bukkitEquipment
            var origin: ItemStack? = null
            val slotStr = playerSlot.slot
            try {
                origin = if (equipmentType != null) {
                    equipmentType.getItem(player)
                } else {
                    val slot = if (slotStr == "held") player.inventory.heldItemSlot else Coerce.toInteger(slotStr)
                    inv.getItem(slot)
                }
            } catch (_: NumberFormatException) {
            }
            if (origin == null || !origin.hasItemMeta() || origin.isAir()) {
                continue
            }
            if (playerSlot.requirements.isNotEmpty() && !playerSlot.requirements.any { origin.hasLore(it) }) {
                continue
            }
            val event = ItemLoadEvent(player, origin)
            event.call()
            if (event.isCancelled) {
                return
            }
            val eventItem = event.itemStack
            if (eventItem.isNotAir() && !eventItem.cacheTag().containsKey("IGNORE_ATTRIBUTE"))
                dataCompound["BASE-EQUIPMENT", playerSlot.key] = eventItem
        }
    }

    private val lores = BaseMap<Int, List<String>>()

    override fun readItemLore(
        itemStack: ItemStack,
        entity: LivingEntity?,
        slot: String?,
    ): AttributeData? {
        if (itemStack.hasLore()) {
            val origin = itemStack.itemMeta?.lore ?: return null
            val hashcode = itemStack.itemMeta?.getProperty<List<String>>("lore").hashCode()
            val lore = lores.map.getOrPut(hashcode) {
                origin.map { it.uncolored() }
            }
            return AttributeSystem.attributeSystemAPI.read(lore, entity, slot)
        }
        return null
    }

    override fun readItemsLore(
        itemStacks: Collection<ItemStack>,
        entity: LivingEntity?,
        slot: String?,
    ): AttributeData {
        return mirrorNow("read-item-lore") {
            val attributeData = AttributeData()
            for (item: ItemStack in itemStacks) {
                attributeData.operation(
                    readItemLore(item, entity, slot) ?: continue
                )
            }
            attributeData
        }
    }

    private fun MutableMap<String, Any>.removeDeep(path: String) {
        val splits = path.split(".")
        if (splits.isEmpty()) {
            this.remove(path)
            return
        }
        var compound = this
        var temp: MutableMap<String, Any>
        for (node in splits) {
            if (node.equals(splits.last(), ignoreCase = true)) {
                compound.remove(node)
            }
            compound[node].also { temp = ((it as MutableMap<String, Any>?) ?: return) }
            compound = temp
        }
    }


    private val nbts = BaseMap<Int, HashMap<String, Any>>()

    @JvmStatic
    private fun ItemTag.toMutableMap(strList: List<String> = emptyList()): MutableMap<String, Any> {
        return nbts.map.getOrPut(keySet().hashCode() + values().hashCode()) {
            val map = HashMap<String, Any>()
            for (it in this) {
                val key = it.key
                if (strList.contains(key)) continue
                val value = it.value.obj()
                map[key] = value
            }
            return map
        }
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    @JvmStatic
    private fun ItemTagData.obj(): Any {
        val value = when (this.type) {
            ItemTagType.BYTE -> this.asByte()
            ItemTagType.SHORT -> this.asShort()
            ItemTagType.INT -> this.asInt()
            ItemTagType.LONG -> this.asLong()
            ItemTagType.FLOAT -> this.asFloat()
            ItemTagType.DOUBLE -> this.asDouble()
            ItemTagType.STRING -> this.asString()
            ItemTagType.BYTE_ARRAY -> this.asByteArray()
            ItemTagType.INT_ARRAY -> this.asIntArray()
            ItemTagType.COMPOUND -> this.asCompound()
            ItemTagType.LIST -> this.asList()
            else -> this.asString()
        }
        return when (value) {
            is ItemTag -> {
                value.toMutableMap()
            }

            is ItemTagList -> {
                val list = ArrayList<Any>()
                value.forEach {
                    list.add(it.obj())
                }
                list
            }

            else -> value
        }
    }

    val itemAttrData = BaseMap<Int, AttributeDataCompound>()
    val tagCache = BaseMap<Int, ItemTag>()

    internal fun ItemStack.cacheTag(): ItemTag {
        if (isAir()) return ItemTag()
        return tagCache.getOrPut(hashCode()) { getItemTag() }
    }

    override fun readItemNBT(
        itemStack: ItemStack,
        entity: LivingEntity?, slot: String?,
    ): AttributeDataCompound? {
        val itemTag = itemStack.cacheTag()
        val attributeDataMap = itemTag["ATTRIBUTE_DATA"]?.asCompound()?.toMutableMap() ?: return null
        val conditionDataMap = itemTag["CONDITION_DATA"]?.asCompound()?.toMutableMap() ?: emptyMap()
        AttributeSystem.conditionManager.conditionNBT(slot, entity, conditionDataMap).forEach {
            attributeDataMap.removeDeep(it)
        }

        return AttributeDataCompound.fromMap(attributeDataMap)
    }

    override fun readItemsNBT(
        itemStacks: Collection<ItemStack>,
        entity: LivingEntity?, slot: String?,
    ): AttributeDataCompound {
        return mirrorNow("read-item-nbt") {
            val attributeDataCompound = AttributeDataCompound(entity)
            for (item: ItemStack in itemStacks) {
                attributeDataCompound.operation(
                    readItemNBT(item, entity) ?: continue
                )
            }
            attributeDataCompound
        }
    }


    override fun readItem(
        itemStack: ItemStack,
        entity: LivingEntity?,
        slot: String?,
    ): AttributeDataCompound {
        val attributeDataCompound = AttributeDataCompound(entity)
        attributeDataCompound["LORE-ATTRIBUTE"] =
            readItemLore(itemStack, entity, slot)?.release() ?: AttributeData().release()
        attributeDataCompound.operation(readItemNBT(itemStack, entity) ?: AttributeDataCompound(entity))

        val event = ItemReadEvent(
            entity ?: return attributeDataCompound,
            itemStack,
            attributeDataCompound,
            slot
        )
        event.call()
        return (if (!event.isCancelled) event.dataCompound else AttributeDataCompound(entity)).also {
            itemAttrData.map.putIfAbsent(itemStack.hashCode() + entity.hashCode(), it)
        }
    }


    override fun readItems(

        itemStacks: Collection<ItemStack>,
        entity: LivingEntity?,
        slot: String?,
    ): AttributeDataCompound {
        return mirrorNow("read-items") {
            val attributeDataCompound = AttributeDataCompound(entity)
            for (item: ItemStack in itemStacks) {
                attributeDataCompound.operation(
                    readItem(item, entity)
                )
            }
            attributeDataCompound
        }
    }

    override fun addEquipment(entity: LivingEntity, key: String, equipments: Map<String, ItemStack>): EquipmentData {
        return addEquipment(entity.uniqueId, key, equipments)
    }

    override fun addEquipment(entity: LivingEntity, key: String, equipments: EquipmentData): EquipmentData {
        return addEquipment(entity.uniqueId, key, equipments)
    }

    override fun addEquipment(uuid: UUID, key: String, equipments: Map<String, ItemStack>): EquipmentData {
        return addEquipment(uuid, key, EquipmentData().apply { putAll(equipments) })
    }


    override fun addEquipment(uuid: UUID, key: String, equipmentData: EquipmentData): EquipmentData {
        return getOrPut(uuid) { EquipmentDataCompound() }.let {
            it[key] = equipmentData
            equipmentData
        }
    }

    override fun removeEquipment(entity: LivingEntity, key: String) {
        removeEquipment(entity.uniqueId, key)
    }

    override fun removeEquipment(uuid: UUID, key: String) {
        get(uuid)?.remove(key)
    }

    override fun put(key: UUID, value: EquipmentDataCompound): EquipmentDataCompound? {
        return super.put(key, value)?.apply { entity = key.livingEntity() }
    }
}
