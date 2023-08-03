package com.skillw.attsystem.internal.manager

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.configManager
import com.skillw.attsystem.AttributeSystem.readManager
import com.skillw.attsystem.api.AttrAPI.read
import com.skillw.attsystem.api.compiled.CompiledAttrData
import com.skillw.attsystem.api.compiled.CompiledAttrDataCompound
import com.skillw.attsystem.api.compiled.oper.ComplexCompiledData
import com.skillw.attsystem.api.manager.CompiledAttrDataManager
import com.skillw.attsystem.util.Utils.validEntity
import com.skillw.pouvoir.util.EntityUtils.livingEntity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import java.util.*

object CompiledAttrDataManagerImpl : CompiledAttrDataManager() {
    override val key = "CompiledAttrDataManager"
    override val priority: Int = 2
    override val subPouvoir = AttributeSystem


    override var playerBaseAttribute = ComplexCompiledData()
    override var entityBaseAttribute = ComplexCompiledData()

    override fun onActive() {
        onReload()
    }

    override fun onReload() {
        playerBaseAttribute.apply {
            clearOperators()
            configManager["config"].getStringList("options.attribute.base-attribute.player").read()?.let { add(it) }
        }

        entityBaseAttribute.apply {
            clearOperators()
            configManager["config"].getStringList("options.attribute.base-attribute.entity").read()?.let { add(it) }
        }
    }

    override fun addCompiledData(
        entity: LivingEntity,
        source: String,
        attributes: Collection<String>,
        slot: String?,
    ): CompiledAttrData? {
        return this.addCompiledData(entity.uniqueId, source, attributes, slot)
    }

    override fun addCompiledData(
        entity: LivingEntity,
        source: String,
        compiledData: CompiledAttrData,
    ): CompiledAttrData {
        val uuid = entity.uniqueId
        this.computeIfAbsent(uuid) {
            CompiledAttrDataCompound().apply {
                val basicAttribute =
                    if (entity is Player) AttributeSystem.compiledAttrDataManager.playerBaseAttribute else AttributeSystem.compiledAttrDataManager.entityBaseAttribute
                register("BASE-ATTRIBUTE", basicAttribute)
            }
        }.register(source, compiledData)
        return compiledData
    }

    override fun addCompiledData(
        uuid: UUID,
        source: String,
        attributes: Collection<String>,
        slot: String?,
    ): CompiledAttrData? {
        return readManager.read(attributes, uuid.livingEntity(), slot)?.let {
            this.addCompiledData(
                uuid,
                source,
                it
            )
        }
    }

    override fun addCompiledData(uuid: UUID, source: String, compiledData: CompiledAttrData): CompiledAttrData {
        return addCompiledData(uuid.validEntity(), source, compiledData)
    }

    override fun removeCompiledData(entity: LivingEntity, source: String): CompiledAttrData? {
        return removeCompiledData(entity.uniqueId, source)
    }

    override fun removeCompiledData(uuid: UUID, source: String): CompiledAttrData? {
        return this[uuid].run {
            remove(source)
        }
    }

    override fun removeIfStartWith(entity: LivingEntity, prefix: String) {
        return removeIfStartWith(entity.uniqueId, prefix)
    }

    override fun get(key: UUID): CompiledAttrDataCompound {
        return computeIfAbsent(key) {
            CompiledAttrDataCompound().apply {
                val entity = key.validEntity()
                val basicAttribute =
                    if (entity is Player) AttributeSystem.compiledAttrDataManager.playerBaseAttribute else AttributeSystem.compiledAttrDataManager.entityBaseAttribute
                register("BASE-ATTRIBUTE", basicAttribute)
            }
        }
    }

    override fun removeIfStartWith(uuid: UUID, prefix: String) {
        val lower = prefix.lowercase()
        this[uuid].run {
            filterKeys { it.startsWith(lower) }.map { it.key }.forEach(this::remove)
        }
    }


}
