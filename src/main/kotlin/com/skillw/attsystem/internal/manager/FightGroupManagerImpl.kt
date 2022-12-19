package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.manager.FightGroupManager
import com.skillw.attsystem.internal.core.fight.FightGroup
import com.skillw.pouvoir.util.FileUtils
import java.io.File

object FightGroupManagerImpl : FightGroupManager() {
    override val key = "FightGroupManager"
    override val priority: Int = 13
    override val subPouvoir = AttributeSystem

    override fun onEnable() {
        onReload()
    }

    override fun onReload() {
        FileUtils.loadMultiply(
            File(AttributeSystem.plugin.dataFolder, "fight_group"), FightGroup::class.java
        ).forEach {
            it.key.register()
        }
    }
}
