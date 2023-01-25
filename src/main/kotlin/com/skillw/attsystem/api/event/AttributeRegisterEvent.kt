package com.skillw.attsystem.api.event

import com.skillw.attsystem.api.attribute.Attribute
import taboolib.platform.type.BukkitProxyEvent

class AttributeRegisterEvent(val attribute: Attribute) : BukkitProxyEvent()