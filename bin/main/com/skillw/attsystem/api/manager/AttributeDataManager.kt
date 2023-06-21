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
     * @param release 是否在下次更新时释放属性数据
     * @return 属性数据
     */
    abstract fun addAttribute(
        entity: LivingEntity,
        key: String,
        attributes: Collection<String>,
        release: Boolean = false,
    ): AttributeData?

    /**
     * Add attribute
     *
     * @param entity 实体
     * @param key 键(源)
     * @param attributeData 属性数据
     * @param release 是否在下次更新时释放属性数据
     * @return 属性数据
     */
    abstract fun addAttribute(
        entity: LivingEntity, key: String, attributeData: AttributeData,
        release: Boolean = false,
    ): AttributeData

    /**
     * Add attribute
     *
     * @param uuid UUID
     * @param key 键(源)
     * @param attributes 字符串集(会据此读取出属性数据)
     * @param release 是否在下次更新时释放属性数据
     * @return 属性数据
     */
    abstract fun addAttribute(
        uuid: UUID, key: String, attributes: Collection<String>,
        release: Boolean = false,
    ): AttributeData?

    /**
     * Add attribute
     *
     * @param uuid UUID
     * @param key 键(源)
     * @param attributeData 属性数据
     * @param release 是否在下次更新时释放属性数据
     * @return 属性数据
     */
    abstract fun addAttribute(
        uuid: UUID, key: String, attributeData: AttributeData,
        release: Boolean = false,
    ): AttributeData

    /**
     * Remove attribute
     *
     * @param entity 实体
     * @param key 键(源)
     */
    abstract fun removeAttribute(entity: LivingEntity, key: String)

    /**
     * Remove attribute
     *
     * @param uuid UUID
     * @param key 键(源)
     */
    abstract fun removeAttribute(uuid: UUID, key: String)

    /** Player base attribute */
    abstract var playerBaseAttribute: AttributeData

    /** Entity base attribute */
    abstract var entityBaseAttribute: AttributeData
}
