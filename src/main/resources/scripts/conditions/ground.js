//@Condition()


Coerce = static("Coerce");

key = "ground";
type = "ALL";
names = ["要求在地面", "要求不在地面"];

function condition(slot, entity, matcher, text) {
    if (entity == null) return true;
    const isIn = !matcher.pattern().toString().contains("不")
    return (isIn && entity.isOnGround()) || (!isIn && !entity.isOnGround())
}

function conditionNBT(slot, entity, map) {
    if (entity == null) return true;
    const isIn = Coerce.toBoolean(map.get("status"))
    return (isIn && entity.isOnGround()) || (!isIn && !entity.isOnGround())
}
