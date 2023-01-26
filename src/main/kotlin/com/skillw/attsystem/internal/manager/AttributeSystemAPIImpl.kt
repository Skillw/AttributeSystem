package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.AttributeSystemAPI
import com.skillw.pouvoir.util.isAlive
import com.skillw.pouvoir.util.livingEntity
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Zombie
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.submit
import taboolib.common.platform.service.PlatformExecutor
import taboolib.common5.Mirror
import java.util.*

object AttributeSystemAPIImpl : AttributeSystemAPI {

    override val key = "AttributeSystemAPI"
    override val priority: Int = 100
    override val subPouvoir = AttributeSystem
    private var task: PlatformExecutor.PlatformTask? = null

    override fun onActive() {
        onReload()
    }

    @Awake(LifeCycle.ACTIVE)
    fun initSystem() {
        submit {
            val world = Bukkit.getWorlds().first()
            val entityA = world.spawnEntity(Location(world, 0.0, 255.0, 0.0), EntityType.ZOMBIE) as Zombie
            val entityB = world.spawnEntity(Location(world, 0.0, 255.0, 0.0), EntityType.ZOMBIE) as Zombie
            entityA.setGravity(false)
            entityB.setGravity(false)
            entityA.damage(1.0, entityB)
            entityA.remove()
            entityB.remove()
        }
        Mirror.mirrorData.clear()
    }

    override fun update(entity: LivingEntity) {
        if (!entity.isAlive()) return
        AttributeSystem.equipmentDataManager.update(entity)
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
//        AttributeSystem.getShieldDataManager().removeByKey(uuid)
    }

}
