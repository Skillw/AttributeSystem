package com.skillw.attsystem.api.equipment

import com.skillw.pouvoir.api.plugin.map.LowerMap
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.getItemTag
import java.util.concurrent.ConcurrentHashMap

/**
 * Equipment data
 *
 * @constructor Create empty Equipment data
 */
class EquipmentData(var compound: EquipmentDataCompound? = null, var source: String? = null) : LowerMap<ItemStack>() {

    private val cache = ConcurrentHashMap<String, Int>()

    constructor(other: EquipmentData, release: Boolean) : this() {
        this.release = release
        for (key in other.keys) {
            this[key] = other[key]!!.clone()
        }
    }

    fun getHead() = get("头盔") ?: get("Head")
    fun getChest() = get("胸甲") ?: get("Chest")
    fun getLegs() = get("护腿") ?: get("Legs")
    fun getFeet() = get("靴子") ?: get("Feet")
    fun getHand() = get("主手") ?: get("Hand")
    fun getOffHand() = get("副手") ?: get("OffHand")


    private fun assertValid() {
        if (compound == null || source == null) {
            error("Operating owner-less EquipmentData! Please register the EquipmentData first!")
        }
    }

    override fun put(key: String, value: ItemStack): ItemStack? {
        assertValid()
        return compound?.let { com ->
            source?.let { sour ->
                com.set(sour, key, value)
            }
        }
    }

    fun uncheckedPut(key: String, value: ItemStack): ItemStack? {
        cache[key] = value.getItemTag(false).toString().hashCode()
        return super.put(key, value)
    }


    override fun remove(key: String): ItemStack? {
        assertValid()
        return compound?.let { com ->
            source?.let { sour ->
                com.removeItem(sour, key)
            }
        }
    }

    internal fun uncheckedRemove(key: String): ItemStack? {
        cache.remove(key)
        return super.remove(key)
    }

    override fun clear() {
        assertValid()
        compound?.let { com ->
            source?.let { sour ->
                com.clear(sour)
            }
        }
    }

    internal fun uncheckedClear() {
        cache.clear()
        super.clear()
    }

    override fun putAll(from: Map<out String, ItemStack>) {
        assertValid()
        compound?.let { com ->
            source?.let { sour ->
                from.forEach { (slot, item) ->
                    com[sour, slot] = item
                }
            }
        }
    }


    /**
     * Clone
     *
     * @return
     */
    fun clone(compound: EquipmentDataCompound?, source: String?): EquipmentData {
        val equipmentData = EquipmentData(compound, source)
        this.forEach {
            equipmentData[it.key] = it.value.clone()
        }
        return equipmentData
    }


    /** Release */
    var release = false

    /**
     * Release
     *
     * 设置为在下次更新时释放装备数据
     *
     * @return
     */
    fun release(): EquipmentData {
        this.release = true
        return this
    }

    /**
     * Un release
     *
     * 设置为不在下次更新时释放装备数据
     *
     * @return
     */
    fun unRelease(): EquipmentData {
        this.release = false
        return this
    }

    fun free(): EquipmentData {
        source = null
        compound = null
        return this
    }

    fun hasChanged(item: ItemStack, slot: String): Boolean {
        return item.getItemTag(false).toString().hashCode() != cache[slot]
    }

}
