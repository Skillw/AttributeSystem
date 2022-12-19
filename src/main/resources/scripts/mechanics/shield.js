Material = org.bukkit.Material;
Player = org.bukkit.entity.Player;
EntityDamageByEntityEvent = org.bukkit.event.entity.EntityDamageByEntityEvent;
EntityDamageEvent = org.bukkit.event.entity.EntityDamageEvent;
Plus = operation("Plus")

//@Mechanic(shield)
function shield(data, context, damageType) {
    const player = data.defender;
    if (!(player instanceof Player)) return 0;

    const event = data.get("event");
    if (!(event instanceof EntityDamageByEntityEvent)) return 0.0;
    //把原版的给扬了
    event.setDamage(EntityDamageEvent.DamageModifier.BLOCKING, 0.0);

    if (!player.isBlocking() || player.getCooldown(Material.SHIELD) > 0.0) {
        return 0.0;
    }

    const enable = data.handle(context.get("enable"));
    if (enable.toString() != "true") {
        return 0.0;
    }
    const reduce = data.handle(context.get("reduce").toString());
    var reduced = 0.0;
    const origin = data.calResult();
    if (reduce >= origin) {
        reduced = origin;
    } else {
        reduced = reduce;
    }
    data.damageSources.put("shield", Plus.element(-reduced));
    const cooldown = data.handle(
        context.get("cooldown").replace("{reduced}", reduced.toString())
    );
    task(function (task) {
        player.setCooldown(Material.SHIELD, cooldown);
    });
    const attacker = data.attacker;
    if (attacker instanceof Player)
        attacker.sendMessage(
            color("&8&l格挡！&f对方格挡了你，减少了&6&l" + reduced + "&f点伤害！")
        );
    player.sendMessage(
        color("&8&l格挡！&f你格挡了对方，减少了&6&l" + reduced + "&f点伤害！")
    );
    return reduced;
}
