package com.skillw.attsystem.api.compiled.oper

import com.skillw.attsystem.api.attribute.compound.AttributeDataCompound
import com.skillw.attsystem.api.compiled.CompiledAttrData
import org.bukkit.entity.LivingEntity
import java.util.*

/**
 * @className ComplexCompiledData
 *
 * @author Glom
 * @date 2023/8/2 21:25 Copyright 2023 user. All rights reserved.
 */
class ComplexCompiledData : CompiledAttrData() {
    private val children = LinkedList<CompiledAttrData>()
    val addition = AttributeDataCompound()
    fun add(operator: CompiledAttrData) {
        children.add(operator)
    }

    override fun putAll(other: CompiledAttrData) {
        super.putAll(other)
        if (other is ComplexCompiledData)
            combine(other)
    }

    fun combine(other: ComplexCompiledData) {
        addition.combine(other.addition)
        children.addAll(other.children)
    }

    override fun clear() {
        super.clear()
        clearOperators()
        addition.clear()
    }

    fun clearOperators() {
        children.clear()
    }

    override fun eval(entity: LivingEntity?): AttributeDataCompound {
        if (!condition(entity)) return AttributeDataCompound()
        val total = AttributeDataCompound(entity)
        if (addition.isNotEmpty()) {
            total.combine(addition.clone())
        }
        children.forEach {
            total.combine(it.eval(entity))
        }
        return total.allToRelease()
    }

    override fun serialize(): MutableMap<String, Any> {
        val total = super.serialize()

        val children = LinkedHashMap<String, Any>()
        this.children.forEach {
            children.putAll(it.serialize())
        }
        return linkedMapOf(
            "ComplexCompiledData-${hashCode()}" to linkedMapOf(
                "conditions" to total,
                "addition" to addition.serialize(),
                "children" to children,
            )
        )
    }

    override fun hashCode(): Int {
        return super.hashCode() + children.hashCode() + addition.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ComplexCompiledData) return false
        if (!super.equals(other)) return false

        if (children != other.children) return false
        return addition == other.addition
    }
}