package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.AttributeSystemAPI
import com.skillw.attsystem.util.Utils.mirrorIfDebug
import com.skillw.pouvoir.util.isAlive
import org.bukkit.entity.LivingEntity
import java.util.*

object AttributeSystemAPIImpl : AttributeSystemAPI {

    override val key = "AttributeSystemAPI"
    override val priority: Int = 100
    override val subPouvoir = AttributeSystem

    override fun update(entity: LivingEntity) {
        if (!entity.isAlive()) return
        mirrorIfDebug("update-entity") {
            mirrorIfDebug("update-equipment") {
                AttributeSystem.equipmentDataManager.update(entity)
            }
            mirrorIfDebug("update-attribute") {
                AttributeSystem.attributeDataManager.update(entity)
            }
            mirrorIfDebug("realize") {
                AttributeSystem.realizerManager.realize(entity)
            }
        }
    }


    override fun remove(entity: LivingEntity) {
        this.remove(entity.uniqueId)
    }


    override fun remove(uuid: UUID) {
        AttributeSystem.attributeDataManager.remove(uuid)
        AttributeSystem.equipmentDataManager.remove(uuid)
        AttributeSystem.compiledAttrDataManager.remove(uuid)
    }

}
