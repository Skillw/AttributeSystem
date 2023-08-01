package com.skillw.attsystem.api.manager

import com.skillw.attsystem.api.attribute.compound.AttributeData
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.BaseMap
import org.bukkit.entity.LivingEntity
import java.util.*

/**
 * Attribute data manager
 *
 * @constructor Create empty Attribute data manager
 */
abstract class AttributeDataManager : BaseMap<UUID, AttributeDataCompound>(), Manager {


    /**
     * EntityUpdate
     *
     * @param entity 实体
     * @return 属性数据集
     */
    abstract fun update(entity: LivingEntity): AttributeDataCompound?

    /**
     * Add attribute
     *
     * @param entity 实体
     * @param key 键(源)
     * @param attributes 字符串集(会据此读取出属性数据)
     * @return 属性数据
     */
    
    abstract fun addAttrData(
        entity: LivingEntity,
        key: String,
        attributes: Collection<String>
    ): AttributeData?

    /**
     * Add attribute
     *
     * @param entity 实体
     * @param key 键(源)
     * @param attributeData 属性数据
     * @return 属性数据
     */
    
    abstract fun addAttrData(
        entity: LivingEntity, key: String, attributeData: AttributeData
    ): AttributeData

    /**
     * Add attribute
     *
     * @param uuid UUID
     * @param key 键(源)
     * @param attributes 字符串集(会据此读取出属性数据)
     * @return 属性数据
     */
    
    abstract fun addAttrData(
        uuid: UUID, key: String, attributes: Collection<String>
    ): AttributeData?

    /**
     * Add attribute
     *
     * @param uuid UUID
     * @param key 键(源)
     * @param attributeData 属性数据
     * @return 属性数据
     */
    
    abstract fun addAttrData(
        uuid: UUID, key: String, attributeData: AttributeData
    ): AttributeData
    
    /**
     * Add attribute
     *
     * @param entity 实体
     * @param key 键(源)
     * @param attributes 字符串集(会据此读取出属性数据)
     * @param release 是否在下次更新时释放属性数据
     * @return 属性数据
     */
    @Deprecated("addAttrData", ReplaceWith("addAttrData(entity, key, attributes)"))
     fun addAttribute(
        entity: LivingEntity,
        key: String,
        attributes: Collection<String>,
        release: Boolean = false,
    ): AttributeData? = addAttrData(entity, key, attributes)

    /**
     * Add attribute
     *
     * @param entity 实体
     * @param key 键(源)
     * @param attributeData 属性数据
     * @param release 是否在下次更新时释放属性数据
     * @return 属性数据
     */
    @Deprecated("addAttrData", ReplaceWith("addAttrData(entity, key, attributeData)"))
    fun addAttribute(
        entity: LivingEntity, key: String, attributeData: AttributeData,
        release: Boolean = false,
    ): AttributeData = addAttrData(entity, key, attributeData)

    /**
     * Add attribute
     *
     * @param uuid UUID
     * @param key 键(源)
     * @param attributes 字符串集(会据此读取出属性数据)
     * @param release 是否在下次更新时释放属性数据
     * @return 属性数据
     */
    @Deprecated("addAttrData", ReplaceWith("addAttrData(uuid, key, attributes)"))
     fun addAttribute(
        uuid: UUID, key: String, attributes: Collection<String>,
        release: Boolean = false,
    ): AttributeData? = addAttrData(uuid, key, attributes)

    /**
     * Add attribute
     *
     * @param uuid UUID
     * @param key 键(源)
     * @param attributeData 属性数据
     * @param release 是否在下次更新时释放属性数据
     * @return 属性数据
     */
    @Deprecated("addAttrData", ReplaceWith("addAttrData(uuid, key, attributeData)"))
     fun addAttribute(
        uuid: UUID, key: String, attributeData: AttributeData,
        release: Boolean = false,
    ): AttributeData = addAttrData(uuid, key, attributeData)

    /**
     * Remove attribute
     *
     * @param entity 实体
     * @param key 键(源)
     */
    @Deprecated("removeAttrData", ReplaceWith("removeAttrData(entity, key)"))
     fun removeAttribute(entity: LivingEntity, key: String) = removeAttrData(entity, key)

    /**
     * Remove attribute
     *
     * @param uuid UUID
     * @param key 键(源)
     */
    @Deprecated("removeAttrData", ReplaceWith("removeAttrData(uuid, key)"))
     fun removeAttribute(uuid: UUID, key: String) = removeAttrData(uuid, key)

    /**
     * Remove attribute
     *
     * @param entity 实体
     * @param key 键(源)
     */
    abstract fun removeAttrData(entity: LivingEntity, key: String)

    /**
     * Remove attribute
     *
     * @param uuid UUID
     * @param key 键(源)
     */
    abstract fun removeAttrData(uuid: UUID, key: String)

    /** Player base attribute */
    abstract var playerBaseAttribute: AttributeData

    /** Entity base attribute */
    abstract var entityBaseAttribute: AttributeData
}
