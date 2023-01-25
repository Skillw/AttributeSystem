package com.skillw.attsystem.internal.feature.listener.update

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.AttrAPI.updateAttr
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.*
import org.spigotmc.event.player.PlayerSpawnLocationEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync

internal object Update {
    private fun LivingEntity.updateAsync(delay: Long = 0) {
        submitAsync(delay = delay) { updateAttr() }
    }

//    @SubscribeEvent
//    fun onPlayerItemChanged(event: PacketSendEvent) {
//        val packet = event.packet
//        if (packet.name != "PacketPlayOutWindowItems" && packet.name != "PacketPlayOutSetSlot") return
//        event.player.updateAsync(2)
//    }

    @SubscribeEvent
    fun onPlayerRespawn(event: PlayerJoinEvent) {
        event.player.updateAsync(2)
    }

    @SubscribeEvent
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        event.player.updateAsync(2)
    }

    @SubscribeEvent
    fun onPlayerSpawnLocation(event: PlayerSpawnLocationEvent) {
        event.player.updateAsync(2)
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun onPlayerPickupItem(event: PlayerPickupItemEvent) {
        event.player.updateAsync(2)
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun onPlayerItemHeld(event: PlayerItemHeldEvent) {
        event.player.updateAsync(2)
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        event.player.updateAsync(2)
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun onPlayerSwapHandItems(event: PlayerSwapHandItemsEvent) {
        event.player.updateAsync(2)
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as Player
        player.updateAsync()
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player as Player
        player.updateAsync()
    }

    @SubscribeEvent
    fun onEntityDead(event: EntityDeathEvent) {
        val entity = event.entity
        if (entity !is Player) {
            AttributeSystem.attributeSystemAPI.remove(entity.uniqueId)
        }
    }
}