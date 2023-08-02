package com.skillw.attsystem.internal.feature.database

import com.skillw.attsystem.internal.manager.ASConfig
import com.skillw.pouvoir.Pouvoir.databaseManager
import com.skillw.pouvoir.api.feature.database.UserBased
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

internal object ASContainer : UserBased {
    @JvmStatic
    val holder by lazy {
        databaseManager.containerHolder(ASConfig.databaseConfig)
    }

    @JvmStatic
    lateinit var container: UserBased

    @Awake(LifeCycle.ENABLE)
    fun loadContainer() {
        kotlin.runCatching {
            container = (holder?.container("as_data", true) as? UserBased?)!!
        }.let {
            if (it.isFailure)
                taboolib.common.platform.function.warning("AttributeSystem User Container Initialization Failed!")
        }
    }

    override fun get(user: String, key: String): String? {
        return container[user, key]
    }

    override fun delete(user: String, key: String) {
        return container.delete(user, key)
    }

    override fun set(user: String, key: String, value: String?) {
        container[user, key] = value
    }

    override fun contains(user: String, key: String): Boolean {
        return container.contains(user, key)
    }
}