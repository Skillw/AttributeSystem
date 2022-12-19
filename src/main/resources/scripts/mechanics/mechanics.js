Player = find("org.bukkit.entity.Player");
Coerce = static("Coerce");
Plus = operation("Plus")
// 燃烧
//@Mechanic(flame)
function flame(data, context, damageType) {
    const enable = data.handle(context.get("enable"));
    if (enable.toString() != "true") {
        return 0.0;
    }
    const attacker = data.attacker;
    const defender = data.defender;
    const damage = Coerce.toDouble(data.handle(context.get("damage")));
    const duration = Coerce.toDouble(data.handle(context.get("duration")));
    task(function (task) {
        defender.setFireTicks(duration);
    });
    data.damageSources.put("fire", Plus.element(damage));
    if (attacker instanceof Player)
        attacker.sendMessage(
            color(
                "&c&l燃烧！&f你点燃了对方，持续 &b" +
                duration / 20 +
                "s &f, 造成了&6&l" +
                damage +
                "&f点伤害！"
            )
        );
    if (defender instanceof Player)
        defender.sendMessage(
            color(
                "&c&l燃烧！&f对方点燃了你，持续 &b" +
                duration / 20 +
                "s &f, 造成了&6&l" +
                damage +
                "&f点伤害！"
            )
        );
    return damage;
}

// 冰冻

//@Mechanic(frozen)
function frozen(data, context, damageType) {
    const enable = data.handle(context.get("enable"));
    if (enable.toString() != "true") {
        return 0.0;
    }
    const attacker = data.attacker;
    const defender = data.defender;
    const value = Coerce.toDouble(data.handle(context.get("value")));
    const duration = Coerce.toDouble(data.handle(context.get("duration")));

    AttrAPI.addAttribute(
        defender,
        "frozen",
        listOf("移动速度: -" + value),
        false
    );
    taskLater([
        duration.longValue(),
        function (task) {
            AttrAPI.removeAttribute(defender, "frozen");
        },
    ]);
    if (attacker instanceof Player)
        attacker.sendMessage(
            color(
                "&b&l冰冻！&f你冰冻了对方，持续 &9" +
                duration / 20 +
                "s &f, 减少了&6&l" +
                value +
                "&f点移动速度！"
            )
        );
    if (defender instanceof Player)
        defender.sendMessage(
            color(
                "&b&l冰冻！&f对方冰冻了你，持续 &9" +
                duration / 20 +
                "s &f, 减少了&6&l" +
                value +
                "&f点移动速度！"
            )
        );
    return value;
}

// 雷击

//@Mechanic(thunder)
function thunder(data, context, damageType) {
    const enable = data.handle(context.get("enable"));
    if (enable.toString() != "true") {
        return 0.0;
    }
    const attacker = data.attacker;
    const defender = data.defender;
    const damage = Coerce.toDouble(data.handle(context.get("damage")));
    task(function (task) {
        defender.world.strikeLightningEffect(defender.location);
    });
    data.damageSources.put("thunder", Plus.element(damage));
    if (attacker instanceof Player)
        attacker.sendMessage(
            color("&e&l雷击！&f你雷击了对方，造成了&6&l" + damage + "&f点伤害！")
        );
    if (defender instanceof Player)
        defender.sendMessage(
            color("&e&l雷击！&f对方雷击了你，造成了&6&l" + damage + "&f点伤害！")
        );
    return damage;
}

// 反弹
//@Mechanic(rebound)
function rebound(data, context, damageType) {
    const enable = data.handle(context.get("enable"));
    if (enable.toString() != "true") {
        return 0.0;
    }
    const attacker = data.attacker;
    const defender = data.defender;
    const multiplier = Coerce.toDouble(data.handle(context.get("multiplier")));
    const damage = data.calResult() * multiplier;
    if (damage <= 0) return 0;
    task(function (task) {
        AttributeSystemAPI.skipNextDamageCal();
        attacker.damage(damage, defender);
    });
    if (attacker instanceof Player)
        attacker.sendMessage(
            color("&e&l反弹！&f对方反弹了你，造成了&6&l" + damage + "&f点伤害！")
        );
    if (defender instanceof Player)
        defender.sendMessage(
            color("&e&l反弹！&f你反弹了对方，造成了&6&l" + damage + "&f点伤害！")
        );
    return damage;
}

PotionEffectType = find("org.bukkit.potion.PotionEffectType");
PotionEffect = find("org.bukkit.potion.PotionEffect");
// 药水
//@Mechanic(potion)
function potion(data, context, damageType) {
    const enable = data.handle(context.get("enable"));
    if (enable.toString() != "true") {
        return 0.0;
    }
    const attacker = data.attacker;
    const defender = data.defender;
    const type = Coerce.toDouble(data.handle(context.get("type")));
    if (Data.containsKey(attacker.uniqueId + type)) return -1;
    const potionType = PotionEffectType.getByName(type);
    if (potionType == null) return -2;
    const level = Coerce.toDouble(data.handle(context.get("level")));
    const duration = Coerce.toDouble(data.handle(context.get("duration")));
    const cooldown = Coerce.toDouble(data.handle(context.get("cooldown")));
    task(function (task) {
        Data.put(attacker.uniqueId + type, true);
        defender.addPotionEffect(new PotionEffect(potionType, duration, level));
    });
    taskLater([
        cooldown,
        function (task) {
            Data.remove(attacker.uniqueId + type);
        },
    ]);
    return duration;
}
