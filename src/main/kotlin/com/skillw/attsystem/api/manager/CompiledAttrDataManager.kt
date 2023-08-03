package com.skillw.attsystem.api.manager

import com.skillw.attsystem.api.compiled.CompiledAttrData
import com.skillw.attsystem.api.compiled.CompiledAttrDataCompound
import com.skillw.attsystem.api.compiled.oper.ComplexCompiledData
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.BaseMap
import org.bukkit.entity.LivingEntity
import java.util.*

/**
 * Attribute data manager
 *
 * @constructor Create empty Attribute data manager
 */
abstract class CompiledAttrDataManager : BaseMap<UUID, CompiledAttrDataCompound>(), Manager {


    /** Player base attribute */
    abstract var playerBaseAttribute: ComplexCompiledData

    /** Entity base attribute */
    abstract var entityBaseAttribute: ComplexCompiledData

    /**
     * 给实体添加预编译属性数据
     *
     * @param entity 实体
     * @param source 源
     * @param attributes 字符串集(会据此读取出预编译属性数据)
     * @return 预编译属性数据
     */

    abstract fun addCompiledData(
        entity: LivingEntity,
        source: String,
        attributes: Collection<String>, slot: String? = null,
    ): CompiledAttrData?

    /**
     * 给实体添加预编译属性数据
     *
     * @param entity 实体
     * @param source 源
     * @param compiledData 预编译属性数据
     * @return 预编译属性数据
     */

    abstract fun addCompiledData(
        entity: LivingEntity, source: String, compiledData: CompiledAttrData,
    ): CompiledAttrData

    /**
     * 给实体添加预编译属性数据
     *
     * @param uuid UUID
     * @param source 源
     * @param attributes 字符串集(会据此读取出预编译属性数据)
     * @return 预编译属性数据
     */

    abstract fun addCompiledData(
        uuid: UUID, source: String, attributes: Collection<String>, slot: String? = null,
    ): CompiledAttrData?

    /**
     * 给实体添加预编译属性数据
     *
     * @param uuid UUID
     * @param source 源
     * @param compiledData 预编译属性数据
     * @return 预编译属性数据
     */

    abstract fun addCompiledData(
        uuid: UUID, source: String, compiledData: CompiledAttrData,
    ): CompiledAttrData


    /**
     * 根据 键(源) 删除实体的预编译属性数据
     *
     * @param entity 实体
     * @param source 键(源)
     */
    abstract fun removeCompiledData(entity: LivingEntity, source: String): CompiledAttrData?

    /**
     * 根据 键(源) 删除实体的预编译属性数据
     *
     * @param uuid UUID
     * @param source 键(源)
     */
    abstract fun removeCompiledData(uuid: UUID, source: String): CompiledAttrData?

    /**
     * 删除所有以 所给前缀 为开头的 预编译属性数据
     *
     * @param uuid UUID
     * @param prefix String 前缀
     */
    abstract fun removeIfStartWith(uuid: UUID, prefix: String)

    /**
     * 删除所有以 所给前缀 为开头的 预编译属性数据
     *
     * @param entity LivingEntity
     * @param prefix String 前缀
     */
    abstract fun removeIfStartWith(entity: LivingEntity, prefix: String)

    abstract override fun get(key: UUID): CompiledAttrDataCompound
}
