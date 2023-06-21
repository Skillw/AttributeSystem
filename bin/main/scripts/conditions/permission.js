//@Condition()

Player = find("org.bukkit.entity.Player");

key = "gm";
type = "ALL";
names = ["权限(:|：)?\\s?(?<permission>.*)"];

function condition(slot, entity, matcher, text) {
    if (entity == null) return true;
    const permission = matcher.group("permission");
    return entity.hasPermission(permission);
}

function conditionNBT(slot, entity, map) {
    if (entity == null || !(entity instanceof Player)) return true;
    const permission = map.get("permission").toString()
    return entity.hasPermission(permission);
}
