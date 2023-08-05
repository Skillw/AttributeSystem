package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.readManager
import com.skillw.attsystem.api.compiled.CompiledAttrDataCompound
import com.skillw.attsystem.api.compiled.CompiledData
import com.skillw.attsystem.api.manager.CompiledAttrDataManager
import com.skillw.attsystem.util.Utils.validEntity
import com.skillw.pouvoir.util.livingEntity
import org.bukkit.entity.LivingEntity
import java.util.*

object CompiledAttrDataManagerImpl : CompiledAttrDataManager() {
    override val key = "CompiledAttrDataManager"
    override val priority: Int = 2
    override val subPouvoir = AttributeSystem
    override fun hasCompiledData(entity: LivingEntity, source: String): Boolean {
        return hasCompiledData(entity.uniqueId, source)
    }

    override fun hasCompiledData(uuid: UUID, source: String): Boolean {
        return get(uuid).containsKey(source)
    }

    override fun addCompiledData(
        entity: LivingEntity,
        source: String,
        attributes: Collection<String>,
        slot: String?,
    ): CompiledData? {
        return this.addCompiledData(entity.uniqueId, source, attributes, slot)
    }

    override fun addCompiledData(
        entity: LivingEntity,
        source: String,
        compiledData: CompiledData,
    ): CompiledData {
        val uuid = entity.uniqueId
        this.computeIfAbsent(uuid) { CompiledAttrDataCompound(entity) }.register(source, compiledData)
        return compiledData
    }

    override fun addCompiledData(
        uuid: UUID,
        source: String,
        attributes: Collection<String>,
        slot: String?,
    ): CompiledData? {
        return readManager.read(attributes, uuid.livingEntity(), slot)?.let {
            this.addCompiledData(
                uuid,
                source,
                it
            )
        }
    }

    override fun addCompiledData(uuid: UUID, source: String, compiledData: CompiledData): CompiledData {
        return addCompiledData(uuid.validEntity(), source, compiledData)
    }

    override fun removeCompiledData(entity: LivingEntity, source: String): CompiledData? {
        return removeCompiledData(entity.uniqueId, source)
    }

    override fun removeCompiledData(uuid: UUID, source: String): CompiledData? {
        return this[uuid].run {
            remove(source)
        }
    }

    override fun removeIfStartWith(entity: LivingEntity, prefix: String) {
        return removeIfStartWith(entity.uniqueId, prefix)
    }

    override fun get(key: UUID): CompiledAttrDataCompound {
        return computeIfAbsent(key) { CompiledAttrDataCompound(key.validEntity()) }
    }

    override fun removeIfStartWith(uuid: UUID, prefix: String) {
        val lower = prefix.lowercase()
        this[uuid].run {
            filterKeys { it.startsWith(lower) }.map { it.key }.forEach(this::remove)
        }
    }


}
