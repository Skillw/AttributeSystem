package com.skillw.attsystem.internal.core.asahi

import com.skillw.asahi.api.annotation.AsahiGetter
import com.skillw.asahi.api.annotation.AsahiSetter
import com.skillw.asahi.api.member.context.AsahiContext
import com.skillw.attsystem.api.AttrAPI.attribute
import com.skillw.attsystem.api.attribute.Attribute
import com.skillw.attsystem.api.attribute.compound.AttributeData
import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.attsystem.api.read.status.GroupStatus
import com.skillw.attsystem.api.read.status.Status
import com.skillw.attsystem.internal.core.read.ReadGroup
import com.skillw.attsystem.internal.feature.compat.pouvoir.AttributePlaceHolder

/**
 * @className Operator
 *
 * @author Glom
 * @date 2023/1/23 17:07 Copyright 2023 user. All rights reserved.
 */

@AsahiGetter
object DataGetter : AsahiContext.Getter("attribute-data", 1) {

    override fun AsahiContext.filter(key: String): Boolean {
        return key.contains(".") && getOrigin(key.split(".")[0]) is AttributeDataCompound
    }

    override fun AsahiContext.getValue(key: String): Any? {
        val varKey = key.split(".")[0]
        val data = getOrigin(varKey)
        val subKeys = key.substringAfter("$varKey.").split(".")
        return when (data) {
            is AttributeDataCompound -> {
                val attribute = attribute(subKeys[0]) ?: return null
                val params = subKeys.toMutableList().apply { removeFirst() }
                AttributePlaceHolder.get(data, attribute, params)
            }

            else -> null
        }
    }
}

@AsahiSetter
object DataSetter : AsahiContext.Setter("attribute-data", 1) {

    override fun AsahiContext.filter(key: String): Boolean {
        return key.contains(".") && getOrigin(key.split(".")[0]) is AttributeDataCompound
    }


    private fun AttributeDataCompound.put(
        source: String,
        attribute: Attribute,
        data: Status<*>,
    ) {

    }

    override fun AsahiContext.setValue(key: String, value: Any?): Any? {
        val varKey = key.split(".")[0]
        val data = getOrigin(varKey)
        val subKeys = key.substringAfter("$varKey.").split(".")
        return when (data) {
            is AttributeDataCompound -> {
                when (subKeys.size) {
                    0 -> {
                        return null
                    }

                    1 -> {
                        val source = subKeys[0]
                        data[source] = AttributeData.fromMap(value as Map<String, Any>)
                    }

                    2 -> {
                        val source = subKeys[0]
                        val attribute = attribute(subKeys[1]) ?: return null
                        if (attribute.readPattern !is ReadGroup<*>) return null
                        val status = attribute.readPattern.readNBT(value as Map<String, Any>, attribute) as Status<*>
                        data.computeIfAbsent(source) { AttributeData() }[attribute] = status

                    }

                    else -> {
                        val source = subKeys[0]
                        val attribute = attribute(subKeys[1]) ?: return null
                        if (attribute.readPattern !is ReadGroup<*>) return null
                        val matcher = subKeys[2]
                        value ?: (data[source, attribute] as? GroupStatus<Any>)?.remove(matcher)
                        value?.let { (data[source, attribute] as? GroupStatus<Any>)?.set(matcher, it) }

                    }

                }
            }

            else -> null
        }
    }
}