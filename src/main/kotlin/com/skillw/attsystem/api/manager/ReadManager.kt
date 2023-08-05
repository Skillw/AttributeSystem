package com.skillw.attsystem.api.manager

import com.skillw.attsystem.api.compiled.CompiledData
import com.skillw.attsystem.api.compiled.sub.NBTCompiledData
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
     * 预读取字符串集的属性数据
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
    ): CompiledData?

    /**
     * 预读取物品 lore 上的属性
     *
     * @param itemStack 物品
     * @param entity 实体
     * @param slot 槽位
     * @return 物品LORE属性数据 无lore则返回null
     */
    abstract fun readItemLore(
        itemStack: ItemStack, entity: LivingEntity? = null, slot: String? = null,
    ): CompiledData?


    /**
     * 预读取物品 lore 上的属性
     *
     * @param itemStacks 物品
     * @param entity 实体
     * @param slot 槽位
     * @return 物品LORE属性数据
     */
    abstract fun readItemsLore(
        itemStacks: Collection<ItemStack>, entity: LivingEntity? = null, slot: String? = null,
    ): CompiledData

    /**
     * 预读取物品 NBT 上的属性
     *
     * @param itemStack 物品
     * @param entity 实体
     * @param slot 槽位
     * @return 物品NBT属性数据集
     */
    abstract fun readItemNBT(
        itemStack: ItemStack, entity: LivingEntity? = null, slot: String? = null,
    ): CompiledData?

    /**
     * 预读取物品 NBT 上的属性
     *
     * @param itemStacks 物品
     * @param entity 实体
     * @param slot 槽位
     * @return 物品NBT属性数据集
     */
    abstract fun readItemsNBT(
        itemStacks: Collection<ItemStack>, entity: LivingEntity? = null, slot: String? = null,
    ): CompiledData


    /**
     * 预读取物品属性 （lore 与 nbt） 都读
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
    ): CompiledData?


    /**
     * 预读取物品属性 （lore 与 nbt） 都读
     *
     * @param itemStacks 物品
     * @param entity 实体
     * @param slot 槽位
     * @return 预编译属性数据
     */
    abstract fun readItems(
        itemStacks: Collection<ItemStack>, entity: LivingEntity? = null, slot: String? = null,
    ): CompiledData

    /**
     * 预读取属性，格式与NBT一样
     *
     * @param attrDataMap MutableMap<String, Any>
     * @param conditions Collection<Any>
     * @param entity LivingEntity?
     * @param slot String?
     * @return CompiledData
     */

    abstract fun readMap(
        attrDataMap: MutableMap<String, Any>,
        conditions: Collection<Any>,
        entity: LivingEntity? = null,
        slot: String? = null,
    ): NBTCompiledData

    /**
     * 预编译属性
     *
     * # map中应包含
     *
     * ```
     * type: nbt / strings(默认)
     * ```
     *
     * ## Type-Strings:
     * ```
     * attributes:
     * - '需要在地面上'
     * - '攻击力: 100 / 需要生命值属性: 10'
     * ```
     *
     * ## Type-NBT:
     * ```
     * attributes:
     *   ababa:
     *     PhysicalDamage:
     *       value: 100
     * conditions:
     *   - conditions:
     *     - key: ground
     *       status: true
     *   - conditions:
     *     - key: attribute
     *       name: 生命值
     *       value: 10
     *     paths: [ "ababa.PhysicalDamage.value" ]
     * ```
     *
     * 以上两个示例的效果是一模一样的
     *
     * @param map Map<String,Any>
     * @param entity LivingEntity?
     * @param slot String?
     * @return CompiledData? 读取失败时返回null
     */

    abstract fun readMap(
        map: Map<String, Any>,
        entity: LivingEntity? = null,
        slot: String? = null,
    ): CompiledData?
}
