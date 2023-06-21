//@Condition()

Coerce = static("Coerce");

key = "weather";
type = "ALL";
names = ["需要天气: (?<weather>.*)", "需要不是天气: (?<weather>.*)"];

function condition(slot, entity, matcher, text) {
    if (entity == null) return true;
    const name = matcher.group("weather")
    const isIn = !matcher.pattern().toString().contains("不")
    return (isIn && entity.getPlayerWeather().name().equalsIgnoreCase(name)) || (!isIn && !entity.getPlayerWeather().name().equalsIgnoreCase(name))
}

function conditionNBT(slot, entity, map) {
    if (entity == null) return true;
    const name = map.get("weather")
    const isIn = !name.contains("!")
    const weather = name.replace("!", "")
    return (isIn && entity.getPlayerWeather().name().equalsIgnoreCase(weather)) || (!isIn && !entity.getPlayerWeather().name().equalsIgnoreCase(weather))
}
