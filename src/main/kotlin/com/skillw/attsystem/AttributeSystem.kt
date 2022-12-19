package com.skillw.attsystem

import com.skillw.attsystem.api.AttributeSystemAPI
import com.skillw.attsystem.api.manager.*
import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.pouvoir.api.annotation.PouManager
import com.skillw.pouvoir.api.annotation.ScriptTopLevel
import com.skillw.pouvoir.api.manager.ManagerData
import com.skillw.pouvoir.api.plugin.SubPouvoir
import com.skillw.pouvoir.api.thread.BasicThreadFactory
import com.skillw.pouvoir.util.MessageUtils.info
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.module.chat.colored
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigFile
import taboolib.module.kether.KetherShell
import taboolib.module.lang.sendLang
import taboolib.platform.BukkitPlugin
import java.util.concurrent.ScheduledThreadPoolExecutor

@ScriptTopLevel
object AttributeSystem : Plugin(), SubPouvoir {

    override val key = "AttributeSystem"

    override lateinit var managerData: ManagerData
    override val plugin by lazy {
        BukkitPlugin.getInstance()
    }
    val poolExecutor: ScheduledThreadPoolExecutor by lazy {
        ScheduledThreadPoolExecutor(
            4,
            BasicThreadFactory.Builder().daemon(true).namingPattern("attribute-system-schedule-pool-%d").build()
        )
    }

    /** Configs */

    @Config("config.yml", migrate = true, autoReload = true)
    lateinit var config: ConfigFile

    @Config("slot.yml", migrate = true, autoReload = true)
    lateinit var slot: ConfigFile

    @Config("formula.yml", migrate = true, autoReload = true)
    lateinit var formula: ConfigFile

    @Config("message.yml", migrate = true, autoReload = true)
    lateinit var message: ConfigFile

    /** Managers */

    @JvmStatic
    @PouManager
    lateinit var configManager: ASConfig

    @ScriptTopLevel("AttributeSystemAPI")
    @JvmStatic
    @PouManager
    lateinit var attributeSystemAPI: AttributeSystemAPI

    @JvmStatic
    @PouManager
    lateinit var readPatternManager: ReadPatternManager

    @ScriptTopLevel("AttributeManager")
    @JvmStatic
    @PouManager
    lateinit var attributeManager: AttributeManager

    @ScriptTopLevel("AttributeDataManager")
    @JvmStatic
    @PouManager
    lateinit var attributeDataManager: AttributeDataManager

    @ScriptTopLevel("EquipmentDataManager")
    @JvmStatic
    @PouManager
    lateinit var equipmentDataManager: EquipmentDataManager

    @JvmStatic
    @PouManager
    lateinit var playerSlotManager: PlayerSlotManager

    @JvmStatic
    @PouManager
    lateinit var entitySlotManager: EntitySlotManager

    @JvmStatic
    @PouManager
    lateinit var conditionManager: ConditionManager

    @JvmStatic
    @PouManager
    lateinit var formulaManager: FormulaManager

    @JvmStatic
    @PouManager
    lateinit var damageTypeManager: DamageTypeManager

    @JvmStatic
    @PouManager
    lateinit var mechanicManager: MechanicManager

    @JvmStatic
    @PouManager
    lateinit var fightGroupManager: FightGroupManager

    @JvmStatic
    @PouManager
    lateinit var realizeManager: RealizeManager

    @JvmStatic
    @PouManager
    lateinit var cooldownManager: CooldownManager

    @JvmStatic
    @PouManager
    lateinit var personalManager: PersonalManager

    @ScriptTopLevel("FightStatusManager")
    @JvmStatic
    @PouManager
    lateinit var fightStatusManager: FightStatusManager

    @JvmStatic
    @PouManager
    lateinit var operationManager: OperationManager

    @JvmStatic
    @PouManager
    lateinit var messageBuilderManager: MessageBuilderManager

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
