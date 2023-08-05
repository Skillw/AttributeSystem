package com.skillw.attsystem.api.manager

import com.skillw.attsystem.AttributeSystem.compiledAttrDataManager
import com.skillw.attsystem.api.attribute.compound.AttributeData
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.attsystem.util.Utils.validEntity
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.plugin.map.BaseMap
import org.bukkit.entity.LivingEntity
import java.util.*

/**
 * Attribute data manager
 *
 * @constructor Create empty Attribute data manager
 */
abstract class AttributeDataManager : BaseMap<UUID, AttributeDataCompound>(), Manager {

    /**
     * 更新实体的属性数据
     *
     * @param entity 实体
     * @return 属性数据集
     */
    abstract fun update(entity: LivingEntity): AttributeDataCompound?


    /**
     * 给实体添加属性数据
     *
     * @param entity 实体
     * @param source 源
     * @param attributeData 属性数据
     * @return 属性数据
     */

    abstract fun addAttrData(
        entity: LivingEntity, source: String, attributeData: AttributeData,
    ): AttributeData

    /**
     * 给实体添加属性数据
     *
     * @param uuid UUID
     * @param source 源
     * @param attributeData 属性数据
     * @return 属性数据
     */

    abstract fun addAttrData(
        uuid: UUID, source: String, attributeData: AttributeData,
    ): AttributeData

    /**
     * 给实体添加属性数据
     *
     * @param entity 实体
     * @param source 源
     * @param attributes 字符串集(会据此读取出属性数据)
     * @param release 是否在下次更新时释放属性数据
     * @return 属性数据
     */
    @Deprecated("addAttrData", ReplaceWith("addAttrData(entity, key, attributes)"))
    fun addAttribute(
        entity: LivingEntity,
        source: String,
        attributes: Collection<String>,
        release: Boolean = false,
    ): AttributeData? =
        compiledAttrDataManager.addCompiledData(entity, source, attributes)?.eval(entity)?.toAttributeData()

    /**
     * 给实体添加属性数据
     *
     * @param entity 实体
     * @param source 源
     * @param attributeData 属性数据
     * @param release 是否在下次更新时释放属性数据
     * @return 属性数据
     */
    @Deprecated("addAttrData", ReplaceWith("addAttrData(entity, key, attributeData)"))
    fun addAttribute(
        entity: LivingEntity, source: String, attributeData: AttributeData,
        release: Boolean = false,
    ): AttributeData = addAttrData(entity, source, attributeData)

    /**
     * 给实体添加属性数据
     *
     * @param uuid UUID
     * @param source 源
     * @param attributes 字符串集(会据此读取出属性数据)
     * @param release 是否在下次更新时释放属性数据
     * @return 属性数据
     */
    @Deprecated("addAttrData", ReplaceWith("addAttrData(uuid, key, attributes)"))
    fun addAttribute(
        uuid: UUID, source: String, attributes: Collection<String>,
        release: Boolean = false,
    ): AttributeData? = uuid.validEntity()?.let { addAttribute(it, source, attributes) }

    /**
     * 给实体添加属性数据
     *
     * @param uuid UUID
     * @param source 源
     * @param attributeData 属性数据
     * @param release 是否在下次更新时释放属性数据
     * @return 属性数据
     */
    @Deprecated("addAttrData", ReplaceWith("addAttrData(uuid, key, attributeData)"))
    fun addAttribute(
        uuid: UUID, source: String, attributeData: AttributeData,
        release: Boolean = false,
    ): AttributeData = addAttrData(uuid, source, attributeData)

    /**
     * 根据 源 删除实体的属性数据
     *
     * @param entity 实体
     * @param source 源
     */
    @Deprecated("removeAttrData", ReplaceWith("removeAttrData(entity, key)"))
    fun removeAttribute(entity: LivingEntity, source: String) = removeAttrData(entity, source)

    /**
     * 根据 源 删除实体的属性数据
     *
     * @param uuid UUID
     * @param source 源
     */
    @Deprecated("removeAttrData", ReplaceWith("removeAttrData(uuid, key)"))
    fun removeAttribute(uuid: UUID, source: String) = removeAttrData(uuid, source)

    /**
     * 根据 源 删除实体的属性数据
     *
     * @param entity 实体
     * @param source 源
     */
    abstract fun removeAttrData(entity: LivingEntity, source: String): AttributeData?

    /**
     * 根据 源 删除实体的属性数据
     *
     * @param uuid UUID
     * @param source 源
     */
    abstract fun removeAttrData(uuid: UUID, source: String): AttributeData?

    override fun get(key: UUID): AttributeDataCompound? {
        return super.get(key)
    }
}
