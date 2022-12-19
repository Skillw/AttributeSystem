//@Condition()

Coerce = static("Coerce");

key = "health";
type = "ALL";
names = ["生命值需要: (?<min>\\d+)-(?<max>\\d+)", "生命值需要: (?<min>\\d+)"];

function condition(slot, entity, matcher, text) {
    if (entity == null) return true;
    const min = Coerce.toDouble(matcher.group("min"))
    let max = min
    try {
        max = Coerce.toDouble(matcher.group("max"))
    } catch (ignored) {
    }
    return entity.health >= min && entity.health <= max
}

function conditionNBT(slot, entity, map) {
    if (entity == null) return true;
    const min = Coerce.toDouble(map.get("min"))
    let max = min
    try {
        max = Coerce.toDouble(map.get("max"))
    } catch (ignored) {
    }
    return entity.health >= min && entity.health <= max
}
