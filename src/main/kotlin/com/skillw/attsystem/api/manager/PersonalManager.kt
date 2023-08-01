package com.skillw.attsystem.api.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.internal.feature.personal.InitialAttrData
import com.skillw.attsystem.internal.feature.personal.PreferenceData
import com.skillw.pouvoir.api.manager.Manager
import org.bukkit.entity.Player
import java.util.*

/**
 * Personal manager
 *
 * @constructor Create empty Personal manager
 */
abstract class PersonalManager : Manager {

    /** Enable */
    abstract val isPreferenceEnable: Boolean

    /**
     * Push data
     *
     * @param player
     */
    abstract fun pushData(player: Player)

    /**
     * Pull data
     *
     * @param uuid
     * @return
     */
    abstract fun pullPreferenceData(uuid: UUID): PreferenceData?

    /**
     * Has data
     *
     * @param player
     * @return
     */
    abstract fun hasPreferenceData(player: Player): Boolean

    companion object {
        internal fun Player.pushData() {
            AttributeSystem.personalManager.pushData(this)
        }

        internal fun UUID.pullPreferenceData(): PreferenceData? =
            AttributeSystem.personalManager.pullPreferenceData(this)

        internal fun UUID.pullInitialAttrData(): InitialAttrData? =
            AttributeSystem.personalManager.pullInitialAttrData(this)

        internal fun Player.hasPreferenceData(): Boolean = AttributeSystem.personalManager.hasPreferenceData(this)
    }

    abstract fun getPreference(key: UUID): PreferenceData
    abstract fun registerPreferenceData(data: PreferenceData)
    abstract fun pullInitialAttrData(uuid: UUID): InitialAttrData?
}
