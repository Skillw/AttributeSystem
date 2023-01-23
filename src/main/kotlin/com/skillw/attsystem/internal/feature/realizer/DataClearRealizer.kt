package com.skillw.attsystem.internal.feature.realizer

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.realizer.component.sub.ScheduledRealizer
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister
import com.skillw.pouvoir.util.livingEntity

@AutoRegister
internal object DataClearRealizer : ScheduledRealizer("data-clear") {

    override val fileName: String = "options.yml"
    override val defaultPeriod: Long = 1200
    override fun task() {
        AttributeSystem.attributeDataManager.keys.forEach {
            val entity = it.livingEntity()
            if (entity?.isValid != true || entity.isDead) AttributeSystem.attributeSystemAPI.remove(it)
        }
    }

}