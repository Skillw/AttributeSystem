package com.skillw.attsystem.internal.feature.compat.pouvoir.function.common

import com.skillw.attsystem.api.AttrAPI.addAttribute
import com.skillw.attsystem.api.AttrAPI.read
import com.skillw.attsystem.api.AttrAPI.readItem
import com.skillw.attsystem.api.AttrAPI.readItemLore
import com.skillw.attsystem.api.AttrAPI.readItemNBT
import com.skillw.attsystem.api.AttrAPI.removeAttribute
import com.skillw.attsystem.api.AttrAPI.update
import com.skillw.attsystem.api.attribute.compound.AttributeData
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.pouvoir.api.annotation.AutoRegister
import com.skillw.pouvoir.api.function.PouFunction
import com.skillw.pouvoir.api.function.parser.Parser
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.ItemTagData
import taboolib.module.nms.getItemTag

@AutoRegister
internal object FunctionAttribute : PouFunction<Any?>("attr", namespace = "common") {

    override fun execute(parser: Parser): Any? {
        with(parser) {
            val entity = context["entity"] as? LivingEntity? ?: parse()
            return when (val main = parseString()) {
                "read" -> {
                    val any = parseList()
                    return any.map { it.toString() }.read(entity)
                }

                "readItem" -> {
                    val item = parse<ItemStack>()
                    return item.readItem(entity)
                }

                "readLore" -> {
                    val item = parse<ItemStack>()
                    return item.readItemLore(entity)
                }

                "readNBT" -> {
                    when (val any = parseAny()) {
                        is ItemStack -> {
                            return any.readItemNBT(entity)
                        }

                        else -> {
                            error("Invalid argument type")
                        }
                    }
                }

                "add" -> {
                    val key = parseString()
                    val attributeData = parse<AttributeData>()
                    entity.addAttribute(key, attributeData)
                }

                "remove" -> {
                    val key = parseString()
                    entity.removeAttribute(key)
                }


                "addItemAttr" -> {
                    val item = parse<ItemStack>()
                    when (val next = parseAny()) {
                        is String -> {
                            val attributeData = parse<AttributeData>()
                            item.getItemTag().apply {
                                putDeep("ATTRIBUTE_DATA.$next", ItemTagData.toNBT(attributeData.serialize()))
                                saveTo(item)
                            }
                        }

                        is AttributeDataCompound -> {
                            next.saveTo(item)
                        }

                        else -> {
                            error("Invalid argument type")
                        }
                    }
                }

                "removeItemAttr" -> {
                    val item = parse<ItemStack>()
                    val key = parseString()
                    item.getItemTag().apply {
                        removeDeep("ATTRIBUTE_DATA.$key")
                        saveTo(item)
                    }
                }

                "update" -> {
                    entity.update()
                    true
                }

                else -> {
                    error("Invalid Attr token $main")
                }
            }
        }
    }
}