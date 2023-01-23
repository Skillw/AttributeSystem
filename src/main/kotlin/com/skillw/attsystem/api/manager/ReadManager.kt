package com.skillw.attsystem.api.manager

import com.skillw.attsystem.api.attribute.compound.AttributeData
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.pouvoir.api.manager.Manager
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

/**
 * Equipment data manager
 *
 * @constructor Create empty Equipment data manager
 */
abstract class ReadManager : Manager {

    /**
     * Read item lore
     *
     * @param itemStack 物品
     * @param entity 实体
     * @param slot 槽位
     * @return 物品LORE属性数据
     */
    abstract fun readItemLore(
        itemStack: ItemStack, entity: LivingEntity? = null, slot: String? = null,
    ): AttributeData?


    /**
     * Read items lore
     *
     * @param itemStacks 物品
     * @param entity 实体
     * @param slot 槽位
     * @return 物品LORE属性数据
     */
    abstract fun readItemsLore(
        itemStacks: Collection<ItemStack>, entity: LivingEntity? = null, slot: String? = null,
    ): AttributeData?

    /**
     * Read item NBT
     *
     * @param itemStack 物品
     * @param entity 实体
     * @param slot 槽位
     * @return 物品NBT属性数据集
     */
    abstract fun readItemNBT(
        itemStack: ItemStack, entity: LivingEntity? = null, slot: String? = null,
    ): AttributeDataCompound?

    /**
     * Read items n b t
     *
     * @param itemStacks 物品
     * @param entity 实体
     * @param slot 槽位
     * @return 物品NBT属性数据集
     */
    abstract fun readItemsNBT(
        itemStacks: Collection<ItemStack>, entity: LivingEntity? = null, slot: String? = null,
    ): AttributeDataCompound?


    /**
     * Read item
     *
     * 读取物品的属性数据集(lore & NBT)
     *
     * 触发ItemReadEvent
     *
     * @param itemStack 物品
     * @param entity 实体
     * @param slot 槽位
     * @return 物品属性数据集
     */
    abstract fun readItem(
        itemStack: ItemStack, entity: LivingEntity? = null, slot: String? = null,
    ): AttributeDataCompound


    /**
     * Read items
     *
     * @param itemStacks 物品
     * @param entity 实体
     * @param slot 槽位
     * @return 物品属性数据集
     */
    abstract fun readItems(
        itemStacks: Collection<ItemStack>, entity: LivingEntity? = null, slot: String? = null,
    ): AttributeDataCompound
}
