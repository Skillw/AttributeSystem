//@Condition()

Player = find("org.bukkit.entity.Player");
Coerce = static("Coerce");

key = "level";
type = "ALL";
names = ["Lv.(?<value>\\d+)", "等级限制:\\s?(?<value>d+)"];

function condition(slot, entity, matcher, text) {
    if (entity == null || !(entity instanceof Player)) return true;
    const level = Coerce.toInteger(matcher.group("value"));
    return entity.level >= level;
}

function conditionNBT(slot, entity, map) {
    if (entity == null || !(entity instanceof Player)) return true;
    const level = Coerce.toInteger(map.get("value"));
    return entity.level >= level;
}
