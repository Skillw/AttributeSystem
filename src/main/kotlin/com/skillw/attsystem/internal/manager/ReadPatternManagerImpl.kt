package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.manager.ReadPatternManager
import com.skillw.attsystem.internal.core.read.BaseReadGroup
import com.skillw.attsystem.internal.manager.ASConfig.debug
import com.skillw.pouvoir.util.loadMultiply
import taboolib.common.platform.function.console
import taboolib.module.lang.sendLang
import java.io.File

object ReadPatternManagerImpl : ReadPatternManager() {
    override val key = "ReadPatternManager"
    override val priority: Int = 1
    override val subPouvoir = AttributeSystem


    override fun onEnable() {
        onReload()
    }

    override fun onReload() {
        this.entries.filter { it.value.release }.forEach { (key, _) ->
            this.remove(key)?.also {
                debug {
                    console().sendLang(
                        "group-reader-unregister",
                        key,
                        it.operations().map { operation -> operation.key }
                    )
                }
            }
        }
        loadMultiply(
            File(AttributeSystem.plugin.dataFolder, "reader"), BaseReadGroup::class.java
        ).forEach {
            debug {
                console().sendLang(
                    "group-reader-register",
                    it.key.key.lowercase(),
                    it.key.operations().map { operation -> operation.key })
            }
            it.key.release = true
            it.key.register()
        }
    }

}
