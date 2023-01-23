package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.attribute.compound.AttributeData
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.attsystem.api.event.ItemReadEvent
import com.skillw.attsystem.api.manager.ReadManager
import com.skillw.pouvoir.api.plugin.map.BaseMap
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import taboolib.common5.mirrorNow
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.module.chat.uncolored
import taboolib.module.nms.*
import taboolib.platform.util.hasLore
import java.util.*

object ReadManagerImpl : ReadManager() {
    override val key = "ReadManager"
    override val priority: Int = 4
    override val subPouvoir = AttributeSystem

    private val lores = BaseMap<Int, List<String>>()

    override fun readItemLore(
        itemStack: ItemStack,
        entity: LivingEntity?,
        slot: String?,
    ): AttributeData? {
        if (itemStack.hasLore()) {
            val origin = itemStack.itemMeta?.lore ?: return null
            val hashcode = itemStack.itemMeta?.getProperty<List<String>>("lore").hashCode()
            val lore = lores.map.computeIfAbsent(hashcode) {
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
        return nbts.map.computeIfAbsent(keySet().hashCode() + values().hashCode()) { _ ->
            val map = HashMap<String, Any>()
            for (it in this) {
                val key = it.key
                if (strList.contains(key)) continue
                val value = it.value.obj()
                map[key] = value
            }
            return@computeIfAbsent map
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


    override fun readItemNBT(
        itemStack: ItemStack,
        entity: LivingEntity?, slot: String?,
    ): AttributeDataCompound? {
        val itemTag = itemStack.getItemTag()
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
        return (if (!event.isCancelled) event.dataCompound else AttributeDataCompound(entity))
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
}
