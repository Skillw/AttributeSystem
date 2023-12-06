package com.skillw.attsystem.internal.core.asahi.prefix

import com.skillw.asahi.api.annotation.AsahiPrefix
import com.skillw.asahi.api.prefixParser
import com.skillw.asahi.api.quest
import com.skillw.asahi.api.quester
import com.skillw.attsystem.api.AttrAPI.addAttribute
import com.skillw.attsystem.api.AttrAPI.getAttrData
import com.skillw.attsystem.api.AttrAPI.read
import com.skillw.attsystem.api.AttrAPI.readItem
import com.skillw.attsystem.api.AttrAPI.readItemLore
import com.skillw.attsystem.api.AttrAPI.readItemNBT
import com.skillw.attsystem.api.AttrAPI.removeAttribute
import com.skillw.attsystem.api.AttrAPI.updateAttr
import com.skillw.attsystem.api.attribute.compound.AttributeData
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.ItemTagData
import taboolib.module.nms.getItemTag


@AsahiPrefix(["attr"])
private fun attr() = prefixParser<Any?> {
    when (val type = next()) {
        "data" -> {
            val entity = if (expect("of")) quest<LivingEntity>() else quester { selector() }
            result {
                entity.get().getAttrData()
            }
        }

        "read" -> {
            val list = quest<List<Any>>()
            val slot = if (expect("slot")) quest<String>() else quester { null }
            result { list.get().map { it.toString() }.read(selectorSafely(), slot.get()) }
        }

        "readItem" -> {
            val item = quest<ItemStack>()
            val slot = if (expect("slot")) quest<String>() else quester { null }
            result { item.get().readItem(selectorSafely(), slot.get()) }
        }

        "readLore" -> {
            val item = quest<ItemStack>()
            val slot = if (expect("slot")) quest<String>() else quester { null }
            result { item.get().readItemLore(selectorSafely(), slot.get()) }
        }

        "readNBT" -> {
            val item = quest<ItemStack>()
            val slot = if (expect("slot")) quest<String>() else quester { null }
            result { item.get().readItemNBT(selectorSafely(), slot.get()) }
        }

        "add" -> {
            val key = quest<String>()
            val attributeData = quest<AttributeData>()
            result { selector<LivingEntity>().addAttribute(key.get(), attributeData.get()) }
        }

        "remove" -> {
            val key = quest<String>()
            result { selector<LivingEntity>().removeAttribute(key.toString()) }
        }


        "addItemAttr" -> {
            val key = quest<String>()
            val attributeData = quest<AttributeData>()
            result {
                selector<ItemStack>().let {
                    it.getItemTag().apply {
                        putDeep("ATTRIBUTE_DATA.${key.get()}", ItemTagData.toNBT(attributeData.get().serialize()))
                        saveTo(it)
                    }
                }
            }
        }

        "addItemAttrs" -> {
            val compound = quest<AttributeDataCompound>()
            result {
                selector<ItemStack>().let {
                    compound.get().saveTo(it)
                }
            }
        }

        "removeItemAttr" -> {
            val key = quest<String>()
            result {
                selector<ItemStack>().let {
                    it.getItemTag().apply {
                        removeDeep("ATTRIBUTE_DATA.${key.get()}")
                        saveTo(it)
                    }
                }
            }
        }

        "update" -> {
            result {
                selector<LivingEntity>().updateAttr()
            }
        }

        else -> {
            error("Invalid Attr token $type")
        }
    }
}