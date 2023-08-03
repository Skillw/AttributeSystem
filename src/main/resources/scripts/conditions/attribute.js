//@Condition()

// load("plugins/Pouvoir/scripts/core/basic.js");

Coerce = static("Coerce");

key = "attribute";
names = ["需要(?<name>.*)属性: (?<value>\\d+)"];

function parameters(matcher, text) {
  const name = matcher.group("name");
  const value = Coerce.toDouble(matcher.group("value"));
  return mapOf({ name: name, value: value });
}

function condition(entity, map) {
  if (entity == null) return true;
  var name = map.get("name");
  var value = map.get("value");
  const compound = AttrAPI.getAttrData(entity);
  return compound == null || compound.getAttrValue(name, "total") >= value;
}
