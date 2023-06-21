//@Condition()
// noinspection InfiniteLoopJS,JSUnresolvedFunction

Coerce = static("Coerce");

key = "attribute";
type = "ALL";
names = ["需要(?<name>.*)属性: (?<value>\\d+)"];

function condition(slot, entity, matcher, text) {
    if (entity == null) return true;
    const name = matcher.group("name");
    const value = Coerce.toDouble(matcher.group("value"));
    const compound = AttrAPI.getAttrData(entity);
    return compound == null || compound.getAttrValue(name, "total") >= value;
}

function conditionNBT(slot, entity, map) {
    if (entity == null) return true;
    for each (var key in map.keySet()) {
        const value = Coerce.toDouble(map.get(key));
        const compound = AttrAPI.getAttrData(entity);
        return compound == null || compound.getAttrValue(key, "total") >= value;
    }
}
