package com.skillw.attsystem

import com.skillw.attsystem.api.AttributeSystemAPI
import com.skillw.attsystem.api.manager.*
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.pouvoir.api.manager.ManagerData
import com.skillw.pouvoir.api.plugin.SubPouvoir
import com.skillw.pouvoir.api.plugin.annotation.PouManager
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.common.platform.function.info
import taboolib.module.chat.colored
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigFile
import taboolib.module.lang.sendLang
import taboolib.module.nms.ItemTag
import taboolib.module.nms.getItemTag
import taboolib.platform.BukkitPlugin

object AttributeSystem : Plugin(), SubPouvoir {

    override val key = "AttributeSystem"
    override lateinit var managerData: ManagerData
    override val plugin by lazy {
        BukkitPlugin.getInstance()
    }

    /** Configs */

    @Config("config.yml", autoReload = true)
    lateinit var config: ConfigFile

    @Config("slot.yml", autoReload = true)
    lateinit var slot: ConfigFile

    @Config("options.yml", autoReload = true)
    lateinit var options: ConfigFile

    @Config("vanilla.yml", autoReload = true)
    lateinit var vanilla: ConfigFile
    /** Managers */

    @JvmStatic
    @PouManager
    lateinit var configManager: ASConfig

    @JvmStatic
    @PouManager
    lateinit var attributeSystemAPI: AttributeSystemAPI

    @JvmStatic
    @PouManager
    lateinit var readPatternManager: ReadPatternManager

    @JvmStatic
    @PouManager
    lateinit var attributeManager: AttributeManager

    @JvmStatic
    @PouManager
    lateinit var attributeDataManager: AttributeDataManager

    @JvmStatic
    @PouManager
    lateinit var equipmentDataManager: EquipmentDataManager

    @JvmStatic
    @PouManager
    lateinit var readManager: ReadManager

    @JvmStatic
    @PouManager
    lateinit var realizerManager: RealizerManager

    @JvmStatic
    @PouManager
    lateinit var compileManager: CompileManager

    @JvmStatic
    @PouManager
    lateinit var compiledAttrDataManager: CompiledAttrDataManager

    override fun onLoad() {
        load()
    }

    override fun onEnable() {
        enable()
    }

    override fun onActive() {
        active()
    }

    override fun onDisable() {
        disable()
    }

    fun debug(string: String) {
        if (configManager.debug) {
            info(string.colored())
        }
    }



    fun debugLang(path: String, vararg args: String) {
        if (configManager.debug) {
            console().sendLang(path, *args)
        }
    }

}
