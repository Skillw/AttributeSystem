//@Condition()

key = "slot";
type = "ALL";
names = ["槽位: (?<slot>.*)"];

function condition(slot, entity, matcher, text) {
    if (entity == null) return true;
    const requiredSlot = matcher.group("slot")
    return requiredSlot.equalsIgnoreCase(slot)
}

function conditionNBT(slot, entity, map) {
    if (entity == null) return true;
    const requiredSlot = map.get("slot")
    return requiredSlot.equalsIgnoreCase(slot)
}
