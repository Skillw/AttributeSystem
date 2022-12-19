package com.skillw.attsystem.internal.feature.compat.pouvoir

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.AttributeSystem.attributeDataManager
import com.skillw.attsystem.AttributeSystem.attributeManager
import com.skillw.attsystem.AttributeSystem.equipmentDataManager
import com.skillw.attsystem.AttributeSystem.formulaManager
import com.skillw.attsystem.api.AttrAPI.getAttrData
import com.skillw.attsystem.api.attribute.Attribute
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.attsystem.api.status.GroupStatus
import com.skillw.attsystem.internal.core.read.ReadGroup
import com.skillw.pouvoir.api.annotation.AutoRegister
import com.skillw.pouvoir.api.placeholder.PouPlaceHolder
import com.skillw.pouvoir.util.NumberUtils.format
import org.bukkit.entity.LivingEntity
import taboolib.platform.util.isAir
import java.math.BigDecimal

@AutoRegister
object AttributePlaceHolder : PouPlaceHolder("as", AttributeSystem) {

    fun get(
        data: AttributeDataCompound,
        attribute: Attribute,
        params: List<String>
    ): String {
        return when (params.size) {
            0 ->
                data.getAttrValue<Any>(attribute)?.toString()
            1 -> {
                data.getAttrValue<Any>(attribute, params[0])?.toString()
            }
            2->{
                val read = attribute.readPattern
                if(read !is ReadGroup<*>) return "0.0"
                (data.getStatus(attribute) as GroupStatus<*>).get(params[1])?.toString()
            }
            else ->
                "0.0"
        } ?: "0.0"
    }
    fun placeholder(params: String, entity: LivingEntity, attrData:AttributeDataCompound): String {
        val lower = params.lowercase().replace(":", "_")
        val uuid = entity.uniqueId
        val strings = if (lower.contains("_")) lower.split("_").toMutableList() else mutableListOf(lower)
        when (strings[0]) {
            "att" -> {
                val attribute = attributeManager[strings[1]]
                attribute?.also {
                    strings.removeAt(0)
                    strings.removeAt(0)
                    return get(attrData, attribute, strings)
                }
            }

            "equipment" -> {
                strings.removeAt(0)
                if (strings.size < 3) return "0.0"
                val key = strings[0]
                val subKey = strings[1]
                val attKey = strings[2]
                strings.removeAt(0)
                strings.removeAt(0)
                strings.removeAt(0)
                val equipment = equipmentDataManager[uuid]
                if (equipment == null || !equipment.containsKey(key)) return "0.0"
                val item = equipment[key, subKey] ?: return "0.0"
                val attribute = attributeManager[attKey] ?: return "0.0"
                if (item.isAir()) return "0.0"
                val itemData = equipmentDataManager.readItem(item)
                return get(itemData, attribute, strings)
            }

            "formula" -> {
                strings.removeAt(0)
                if (strings.isEmpty()) return "0.0"
                return BigDecimal(formulaManager.calculate(uuid, strings[0])).format()
            }

            "formulastr" -> {
                strings.removeAt(0)
                if (strings.isEmpty()) return "0.0"
                return formulaManager[strings[0]] ?: "N/A"
            }
        }
        return "0.0"
    }

    override fun onPlaceHolderRequest(params: String, entity: LivingEntity, def: String): String {
        return placeholder(params, entity, entity.getAttrData() ?: return "NULL_DATA")
    }
}
