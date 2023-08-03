package com.skillw.attsystem.api.manager

import com.skillw.attsystem.api.compiled.CompiledAttrData
import com.skillw.pouvoir.api.manager.Manager
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

/**
 * ReadManager
 *
 * @constructor Create empty ReadManager
 */
abstract class ReadManager : Manager {
    /**
     * Read
     *
     * 读取字符串集的属性数据
     *
     * @param strings 待读取属性的字符串集
     * @param entity 实体
     * @param slot 槽位(可为null)
     * @return 预编译属性数据 （StringsReadEvent事件被取消时返回null)
     */
    abstract fun read(
        strings: Collection<String>,
        entity: LivingEntity? = null,
        slot: String? = null,
    ): CompiledAttrData?

    /**
     * 读取物品 lore 上的属性
     *
     * @param itemStack 物品
     * @param entity 实体
     * @param slot 槽位
     * @return 物品LORE属性数据 无lore则返回null
     */
    abstract fun readItemLore(
        itemStack: ItemStack, entity: LivingEntity? = null, slot: String? = null,
    ): CompiledAttrData?


    /**
     * 读取物品 lore 上的属性
     *
     * @param itemStacks 物品
     * @param entity 实体
     * @param slot 槽位
     * @return 物品LORE属性数据
     */
    abstract fun readItemsLore(
        itemStacks: Collection<ItemStack>, entity: LivingEntity? = null, slot: String? = null,
    ): CompiledAttrData

    /**
     * 读取物品 NBT 上的属性
     *
     * @param itemStack 物品
     * @param entity 实体
     * @param slot 槽位
     * @return 物品NBT属性数据集
     */
    abstract fun readItemNBT(
        itemStack: ItemStack, entity: LivingEntity? = null, slot: String? = null,
    ): CompiledAttrData?

    /**
     * 读取物品 NBT 上的属性
     *
     * @param itemStacks 物品
     * @param entity 实体
     * @param slot 槽位
     * @return 物品NBT属性数据集
     */
    abstract fun readItemsNBT(
        itemStacks: Collection<ItemStack>, entity: LivingEntity? = null, slot: String? = null,
    ): CompiledAttrData


    /**
     * 读取物品属性 （lore 与 nbt） 都读
     *
     * 读取物品的属性数据集(lore & NBT)
     *
     * 触发ItemReadEvent
     *
     * @param itemStack 物品
     * @param entity 实体
     * @param slot 槽位
     * @return 预编译属性数据 （ItemReadEvent事件被取消时返回null)
     */
    abstract fun readItem(
        itemStack: ItemStack, entity: LivingEntity? = null, slot: String? = null,
    ): CompiledAttrData?


    /**
     * 读取物品属性 （lore 与 nbt） 都读
     *
     * @param itemStacks 物品
     * @param entity 实体
     * @param slot 槽位
     * @return 预编译属性数据
     */
    abstract fun readItems(
        itemStacks: Collection<ItemStack>, entity: LivingEntity? = null, slot: String? = null,
    ): CompiledAttrData

    abstract fun readMap(
        attrDataMap: MutableMap<String, Any>,
        conditions: Collection<Any>,
        entity: LivingEntity?,
        slot: String?,
    ): CompiledAttrData
}
