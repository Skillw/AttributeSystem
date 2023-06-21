package com.skillw.attsystem.api.manager

import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.BaseMap
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * Cooldown manager
 *
 * @constructor Create empty Cooldown manager
 */
abstract class CooldownManager : BaseMap<UUID, BaseMap<Material, CooldownManager.CooldownTime>>(), Manager {

    /**
     * Cooldown time
     *
     * @constructor Create empty Cooldown time
     * @property total 冷却时间 秒
     */
    class CooldownTime(time: Double) {
        val total = time * 1000

        /** Start */
        val start = System.currentTimeMillis()
    }


    /**
     * 删除
     *
     * @param key UUID
     * @param material 材质
     */
    abstract fun remove(key: UUID, material: Material)

    /**
     * Remove
     *
     * @param player 玩家
     * @param material 材质
     */
    abstract fun remove(player: Player, material: Material)

    /**
     * 拉取玩家 已度过冷却时间/总冷却时间
     *
     * @param key UUID
     * @param material 材质
     * @return 已度过冷却时间/总冷却时间
     */
    abstract fun pull(key: UUID, material: Material): Double

    /**
     * 拉取玩家 已度过冷却时间/总冷却时间 （会删除）
     *
     * @param player 玩家
     * @param material 材质
     * @return 已度过冷却时间/总冷却时间
     */
    abstract fun pull(player: Player, material: Material): Double

    /**
     * 拉取玩家某个材质的冷却 (tick)
     *
     * @param player 玩家
     * @param material 材质
     * @return 冷却时间(tick)
     */
    abstract fun getItemCoolDown(player: Player, material: Material): Int

    /**
     * Get item cool down
     *
     * @param player 玩家
     * @param slot 槽位
     * @return 冷却时间(tick)
     */
    abstract fun getItemCoolDown(player: Player, slot: Int): Int

    /**
     * Get item cool down
     *
     * @param player 玩家
     * @param itemStack 物品
     * @return 冷却时间(tick)
     */
    abstract fun getItemCoolDown(player: Player, itemStack: ItemStack): Int

    /**
     * Set item cool down
     *
     * @param player 玩家
     * @param material 材质
     * @param attackSpeed 攻击速度 (秒)
     */
    abstract fun setItemCoolDown(player: Player, material: Material, attackSpeed: Double)

    /**
     * Set item cool down
     *
     * @param player 玩家
     * @param slot 槽位
     * @param attackSpeed 攻击速度 (秒)
     */
    abstract fun setItemCoolDown(player: Player, slot: Int, attackSpeed: Double)

    /**
     * Set item cool down
     *
     * @param player 玩家
     * @param itemStack 物品
     * @param attackSpeed 攻击速度 (秒)
     */
    abstract fun setItemCoolDown(player: Player, itemStack: ItemStack, attackSpeed: Double)

    /**
     * Is item cool down
     *
     * @param player 玩家
     * @param itemStack 物品
     * @return 是否冷却中
     */
    abstract fun isItemCoolDown(player: Player, itemStack: ItemStack): Boolean

    /**
     * Is item cool down
     *
     * @param player 玩家
     * @param slot 槽位
     * @return 是否冷却中
     */
    abstract fun isItemCoolDown(player: Player, slot: Int): Boolean

// 给大家看看铸币写的
//    companion object {
//
//        internal fun UUID.push(material: Material, time: Double) =
//            AttributeSystem.cooldownManager.push(this, material, time)
//
//        internal fun Player.push(material: Material, time: Double) =
//            AttributeSystem.cooldownManager.push(this, material, time)
//
//        internal fun UUID.remove(material: Material) = AttributeSystem.cooldownManager.remove(this, material)
//        internal fun Player.remove(material: Material) = AttributeSystem.cooldownManager.remove(this, material)
//
//        internal fun UUID.pull(material: Material): Double = AttributeSystem.cooldownManager.pull(this, material)
//        internal fun Player.pull(material: Material): Double = AttributeSystem.cooldownManager.pull(this, material)
//
//        internal fun Player.getItemCoolDown(material: Material): Int =
//            AttributeSystem.cooldownManager.getItemCoolDown(this, material)
//
//        internal fun Player.getItemCoolDown(slot: Int): Int =
//            AttributeSystem.cooldownManager.getItemCoolDown(this, slot)
//
//        internal fun Player.getItemCoolDown(itemStack: ItemStack): Int =
//            AttributeSystem.cooldownManager.getItemCoolDown(this, itemStack)
//
//        internal fun Player.getItemCoolDown(): Int =
//            AttributeSystem.cooldownManager.getItemCoolDown(this, this.inventory.itemInMainHand)
//
//
//        internal fun Player.setItemCoolDown(material: Material, attackSpeed: Double) =
//            AttributeSystem.cooldownManager.setItemCoolDown(this, material, attackSpeed)
//
//        internal fun Player.setItemCoolDown(slot: Int, attackSpeed: Double) =
//            AttributeSystem.cooldownManager.setItemCoolDown(this, slot, attackSpeed)
//
//        internal fun Player.setItemCoolDown(itemStack: ItemStack, attackSpeed: Double) =
//            AttributeSystem.cooldownManager.setItemCoolDown(this, itemStack, attackSpeed)
//
//        internal fun Player.setItemCoolDown(attackSpeed: Double) =
//            AttributeSystem.cooldownManager.setItemCoolDown(this, this.inventory.itemInMainHand, attackSpeed)
//
//        internal fun Player.isItemCoolDown(itemStack: ItemStack): Boolean =
//            AttributeSystem.cooldownManager.isItemCoolDown(this, itemStack)
//
//        fun Player.isItemCoolDown(slot: Int): Boolean = AttributeSystem.cooldownManager.isItemCoolDown(this, slot)
//        fun Player.isItemCoolDown(): Boolean =
//            AttributeSystem.cooldownManager.isItemCoolDown(this, this.inventory.itemInMainHand)
//    }
}
