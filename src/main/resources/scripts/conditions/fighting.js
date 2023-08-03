//@Condition()

Player = find("org.bukkit.entity.Player");
Coerce = static("Coerce");

key = "fighting";

names = ["需要战斗状态", "需要不在战斗"];

function parameters(matcher, text) {
  const isIn = !matcher.pattern().toString().contains("不");
  return mapOf({ status: isIn });
}

function condition(entity, map) {
  if (entity == null) return true;
  const isIn = Coerce.toBoolean(map.get("status"));
  return (
    (isIn && AttributeSystem.fightStatusManager.isFighting(entity)) ||
    (!isIn && !AttributeSystem.fightStatusManager.isFighting(entity))
  );
}
