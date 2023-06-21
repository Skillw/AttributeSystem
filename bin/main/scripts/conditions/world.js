//@Condition()

Coerce = static("Coerce");

key = "world";
type = "ALL";
names = ["要求世界: (?<world>.*)", "需要不是世界: (?<world>.*)"];

function condition(slot, entity, matcher, text) {
    if (entity == null) return true;
    const name = matcher.group("world")
    const isIn = !matcher.pattern().toString().contains("不")
    return (isIn && entity.location.world.name.equalsIgnoreCase(name)) || (!isIn && !entity.location.world.name.equalsIgnoreCase(name))
}

function conditionNBT(slot, entity, map) {
    if (entity == null) return true;
    const name = map.get("world")
    const isIn = !name.contains("!")
    const worldName = name.replace("!", "")
    return (isIn && entity.location.world.name.equalsIgnoreCase(worldName)) || (!isIn && !entity.location.world.name.equalsIgnoreCase(worldName))
}
