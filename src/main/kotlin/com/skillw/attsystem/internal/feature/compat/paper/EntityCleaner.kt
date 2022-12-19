package com.skillw.attsystem.internal.feature.compat.paper

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import com.skillw.attsystem.AttributeSystem
import taboolib.common.platform.event.OptionalEvent
import taboolib.common.platform.event.SubscribeEvent

object EntityCleaner {
    @SubscribeEvent(bind = "com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent")
    fun onEntityDead(optionalEvent: OptionalEvent) {
        val event = optionalEvent.get<EntityRemoveFromWorldEvent>()
        val livingEntity = event.entity
        AttributeSystem.attributeSystemAPI.remove(livingEntity.uniqueId)
    }
}