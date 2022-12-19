//@Condition()


Coerce = static("Coerce");

key = "water";
type = "ALL";
names = ["需要在水里", "需要不在水里"];

function condition(slot, entity, matcher, text) {
    if (entity == null) return true;
    const isIn = !matcher.pattern().toString().contains("不")
    return (isIn && entity.isInWaterOrRain()) || (!isIn && !entity.isInWaterOrRain())
}

function conditionNBT(slot, entity, map) {
    if (entity == null) return true;
    const isIn = Coerce.toBoolean(map.get("status"))
    return (isIn && entity.isInWaterOrRain()) || (!isIn && !entity.isInWaterOrRain())
}
