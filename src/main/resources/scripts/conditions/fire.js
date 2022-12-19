//@Condition()

Player = find("org.bukkit.entity.Player");
Coerce = static("Coerce");

key = "fire";
type = "ALL";
names = ["需要在燃烧", "需要不在燃烧"];

function condition(slot, entity, matcher, text) {
    if (entity == null) return true;
    const isIn = !matcher.pattern().toString().contains("不")
    return (isIn && entity.isOnFire()) || (!isIn && !entity.isOnFire())
}

function conditionNBT(slot, entity, map) {
    if (entity == null) return true;
    const isIn = Coerce.toBoolean(map.get("status"))
    return (isIn && entity.fireTicks > 0) || (!isIn && entity.fireTicks <= 0)
}
