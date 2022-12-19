//@Condition()

Player = find("org.bukkit.entity.Player");
Coerce = static("Coerce");

key = "food";
type = "ALL";
names = ["要求饱食度: (?<value>\\d+)"];

function condition(slot, entity, matcher, text) {
    if (entity == null || !(entity instanceof Player)) return true;
    const level = Coerce.toInteger(matcher.group("value"));
    return entity.foodLevel >= level;
}

function conditionNBT(slot, entity, map) {
    if (entity == null || !(entity instanceof Player)) return true;
    const level = Coerce.toInteger(map.get("value"));
    return entity.foodLevel >= level;
}
