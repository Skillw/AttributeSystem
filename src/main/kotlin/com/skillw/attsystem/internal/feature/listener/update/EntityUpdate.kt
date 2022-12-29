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
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.submitAsync

private object EntityUpdate {
    private fun LivingEntity.updateAsync() {
        submitAsync { updateAttr() }
    }

    @SubscribeEvent
    fun onPlayerRespawn(event: PlayerJoinEvent) {
        event.player.updateAttr()
    }

    @SubscribeEvent
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        event.player.updateAttr()
    }

    @SubscribeEvent
    fun onPlayerSpawnLocation(event: PlayerSpawnLocationEvent) {
        event.player.updateAttr()
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun onPlayerPickupItem(event: PlayerPickupItemEvent) {
        event.player.updateAttr()
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun onPlayerItemHeld(event: PlayerItemHeldEvent) {
        submit(delay = 1) { event.player.updateAttr() }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        event.player.updateAttr()
    }


    @SubscribeEvent(ignoreCancelled = true)
    fun onPlayerSwapHandItems(event: PlayerSwapHandItemsEvent) {
        submit(delay = 1) { event.player.updateAttr() }
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