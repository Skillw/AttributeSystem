//@Condition()

key = "slot";

names = ["槽位: (?<slot>.*)"];

function parameters(matcher, text) {
  var requiredSlot = matcher.group("slot");
  return mapOf({ required: requiredSlot });
}

function condition(entity, map) {
  if (entity == null) return true;
  // 槽位会自动初始化到参数中
  const slot = map.get("slot");
  const required = map.get("required");
  if (slot == null || required == null) return true;
  return slot.equalsIgnoreCase(required);
}
