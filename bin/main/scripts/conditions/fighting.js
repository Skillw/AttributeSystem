//@Condition()

Player = find("org.bukkit.entity.Player");
Coerce = static("Coerce");

key = "fighting";
type = "ALL";
names = ["需要战斗状态", "需要不在战斗"];

function condition(slot, entity, matcher, text) {
    if (entity == null) return true;
    const isIn = !matcher.pattern().toString().contains("不")
    return (isIn && AttributeSystem.fightStatusManager.isFighting(entity)) || (!isIn && !AttributeSystem.fightStatusManager.isFighting(entity))
}

function conditionNBT(slot, entity, map) {
    if (entity == null) return true;
    const isIn = Coerce.toBoolean(map.get("status"))
    return (isIn && AttributeSystem.fightStatusManager.isFighting(entity)) || (!isIn && !AttributeSystem.fightStatusManager.isFighting(entity))
}
