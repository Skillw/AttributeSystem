package com.skillw.attsystem.api.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.internal.feature.personal.PersonalData
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.KeyMap
import org.bukkit.entity.Player
import java.util.*

/**
 * Personal manager
 *
 * @constructor Create empty Personal manager
 */
abstract class PersonalManager : KeyMap<UUID, PersonalData>(), Manager {

    /** Enable */
    abstract val enable: Boolean

    /**
     * Push data
     *
     * @param player
     */
    abstract fun pushData(player: Player)

    /**
     * Pull data
     *
     * @param player
     * @return
     */
    abstract fun pullData(player: Player): PersonalData?

    /**
     * Has data
     *
     * @param player
     * @return
     */
    abstract fun hasData(player: Player): Boolean

    companion object {
        internal fun Player.pushData() {
            AttributeSystem.personalManager.pushData(this)
        }

        internal fun Player.pullData(): PersonalData? = AttributeSystem.personalManager.pullData(this)
        internal fun Player.hasData(): Boolean = AttributeSystem.personalManager.hasData(this)
    }
}
