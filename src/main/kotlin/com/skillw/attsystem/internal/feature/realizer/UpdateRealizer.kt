package com.skillw.attsystem.internal.feature.realizer

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.attributeDataManager
import com.skillw.attsystem.AttributeSystem.realizerManager
import com.skillw.attsystem.api.AttrAPI.update
import com.skillw.attsystem.api.realizer.component.ScheduledRealizer
import com.skillw.attsystem.internal.manager.AttributeSystemAPIImpl.remove
import com.skillw.attsystem.util.Utils.validEntity
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.*
import org.spigotmc.event.player.PlayerSpawnLocationEvent
import taboolib.common.platform.event.OptionalEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync
import taboolib.common5.Baffle
import java.util.concurrent.TimeUnit

@AutoRegister
internal object UpdateRealizer : ScheduledRealizer("update", true) {
    override val file by lazy {
        AttributeSystem.options.file!!
    }

    override val defaultPeriod: Long = 10

    override fun task() {
        for (uuid in attributeDataManager.keys) {
            val entity = uuid.validEntity()
            if (entity == null || !entity.isValid || entity.isDead) {
                remove(uuid)
                continue
            }
            entity.update()
        }
        realizerManager.executeSyncTasks()
    }

    @SubscribeEvent(bind = "com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent")
    fun onEntityDead(optionalEvent: OptionalEvent) {
        val event = optionalEvent.get<EntityRemoveFromWorldEvent>()
        val entity = event.entity
        AttributeSystem.attributeSystemAPI.remove(entity.uniqueId)
    }


    @SubscribeEvent
    fun onEntityDead(event: EntityDeathEvent) {
        val entity = event.entity
        if (entity !is Player) {
            AttributeSystem.attributeSystemAPI.remove(entity.uniqueId)
        }
    }


    private var baffle = Baffle.of(40, TimeUnit.MILLISECONDS)
//
//    override fun onEnable() {
//        onReload()
//    }
//
//    override fun onReload() {
//        baffle.resetAll()
//        baffle = Baffle.of(config.getOrDefault("baffle", 40).cint)
//    }

    internal fun LivingEntity.updateAsync(delay: Long = 0) {
        if (baffle.hasNext(name)) {
            submitAsync(delay = delay) {
                update()
            }
        }
    }

    @SubscribeEvent
    fun join(event: PlayerJoinEvent) {
        event.player.run {
            updateAsync(10)
        }
    }

    @SubscribeEvent
    fun respawn(event: PlayerRespawnEvent) {
        event.player.updateAsync(2)
    }

    @SubscribeEvent
    fun spawnLocation(event: PlayerSpawnLocationEvent) {
        event.player.updateAsync(2)
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun pickupItem(event: PlayerPickupItemEvent) {
        event.player.updateAsync(2)
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun itemHeld(event: PlayerItemHeldEvent) {
        event.player.updateAsync(2)
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun dropItem(event: PlayerDropItemEvent) {
        event.player.updateAsync(2)
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun swapHandItems(event: PlayerSwapHandItemsEvent) {
        event.player.updateAsync(2)
    }


    @SubscribeEvent(ignoreCancelled = true)
    fun click(event: InventoryClickEvent) {
        val player = event.whoClicked as Player
        player.updateAsync(2)
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun close(event: InventoryCloseEvent) {
        val player = event.player as Player
        player.updateAsync(2)
    }

    @SubscribeEvent
    fun quit(event: PlayerQuitEvent) {
        val player = event.player
        AttributeSystem.attributeSystemAPI.remove(player.uniqueId)
        baffle.reset(player.name)
    }
}