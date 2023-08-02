package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.AttributeSystemAPI
import com.skillw.pouvoir.util.isAlive
import com.skillw.pouvoir.util.livingEntity
import org.bukkit.entity.LivingEntity
import java.util.*

object AttributeSystemAPIImpl : AttributeSystemAPI {

    override val key = "AttributeSystemAPI"
    override val priority: Int = 100
    override val subPouvoir = AttributeSystem

    override fun onActive() {
        onReload()
    }

    override fun update(entity: LivingEntity) {
        if (!entity.isAlive()) return
        AttributeSystem.equipmentDataManager.update(entity)
        //第一次更新无条件的属性
        AttributeSystem.attributeDataManager.update(entity)
        //第一次更新有条件的属性（有些条件是以其它属性为基础）
        AttributeSystem.attributeDataManager.update(entity)
        AttributeSystem.realizerManager.realize(entity)
    }


    override fun remove(entity: LivingEntity) {
        this.remove(entity.uniqueId)
    }


    override fun remove(uuid: UUID) {
        AttributeSystem.attributeDataManager.remove(uuid)
        AttributeSystem.equipmentDataManager.remove(uuid)
        uuid.livingEntity()?.let { AttributeSystem.realizerManager.unrealize(it) }
    }

}
