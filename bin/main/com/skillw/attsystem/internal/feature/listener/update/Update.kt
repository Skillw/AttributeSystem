package com.skillw.attsystem.internal.feature.listener.update

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.AttrAPI.updateAttr
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.*
import org.spigotmc.event.player.PlayerSpawnLocationEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync
import taboolib.common5.Baffle
import java.util.concurrent.TimeUnit

internal object Update {
    private val baffle = Baffle.of(50, TimeUnit.MILLISECONDS)
    internal fun LivingEntity.updateAsync(delay: Long = 0) {
        if (baffle.hasNext(name))
            submitAsync(delay = delay) { updateAttr() }
    }

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
        player.updateAsync(2)
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player as Player
        player.updateAsync(2)
    }

    @SubscribeEvent
    fun onPlayerDead(event: PlayerDeathEvent) {
        val entity = event.entity
        AttributeSystem.attributeSystemAPI.remove(entity.uniqueId)
    }

    @SubscribeEvent
    fun onEntityDead(event: EntityDeathEvent) {
        val entity = event.entity
        baffle.reset(entity.name)
        if (entity !is Player) {
            AttributeSystem.attributeSystemAPI.remove(entity.uniqueId)
        }
    }

    @SubscribeEvent
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        AttributeSystem.attributeSystemAPI.remove(player.uniqueId)
        baffle.reset(player.name)
    }
}