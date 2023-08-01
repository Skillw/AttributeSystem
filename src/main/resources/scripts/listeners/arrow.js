load("plugins/Pouvoir/scripts/core/util/color.js");

MinecraftVersion = static("MinecraftVersion").INSTANCE;

function conf() {
    return {
        //弓箭属性计算公式均为 "属性值/100"
        enable: true,
        //视角惯性
        inertia: {
            //若开启则抛射物会受玩家视角转动的线速度影响
            enable: false,
            coefficient: 0.5,
        },
        //蓄力影响
        //拉满则可以打出满射速的箭,如果没拉满则按力道削弱速度
        //拉满精准度会提高,没拉满精准度则会降低,精准度不会超过 默认值+属性加成
        force: true,
        //默认箭飞行速度 (数值越大速度越快)
        speed: 2.0,
        //默认箭飞行偏离 (数值越大偏离越大)
        spread: 3.0,
        //穿透 1.14+ 版本支持,其他版本爬
        through: {
            //是否启用
            enable: true,
            //穿透次数 (箭术穿透触发时穿透实体次数)
            amount: "%as_att:ArrowPierceAmount%",
        },
    };
}

//@Listener(-event org.bukkit.event.entity.EntityShootBowEvent)
function shootArrow(event) {
    let config = conf();
    if (!config.enable) return;
    const entity = event.entity;
    const attrData = AttrAPI.getAttrData(entity);
    if (attrData == null) return;
    const arrow = event.projectile;
    let speed = config.speed + attrData.getAttrValue("ArrowSpeed", "total");
    let spread = Math.max(
        0.0,
        config.spread - attrData.getAttrValue("ArrowAccurate", "total")
    );
    if (config.force) {
        speed *= event.force;
        spread *= event.force;
    }
    if (MinecraftVersion.major >= 6 && config.through.enable) {
        const throughChance = attrData.getAttrValue("ArrowPierceChance", "total");
        if (Math.random() < throughChance)
            arrow.pierceLevel = calculate(config.through.amount, entity);
    }
    const velocity = arrow.velocity;
    const handle = arrow.getHandle();
    if (MinecraftVersion.major <= 8) {
        handle.shoot(velocity.x, velocity.y, velocity.z, speed, spread);
    } else {
        handle.shoot(velocity.x, velocity.y, velocity.z, speed, spread);
    }
}

//@Awake(Enable)
//@Awake(Reload)
function load() {
    print(color("&5[&9arrow.js&5] &aLoading Real Projectile..."));
    var task = Data.get("inertia-task");
    if (task != null) {
        task.cancel();
    }
    let config = conf();
    if (!config.inertia.enable) {
        print(color("&5[&9arrow.js&5] &cDisabling Real Projectile..."));
        return;
    }
    Data.put(
        "inertia-task",
        Tool.taskAsyncTimer(10, 1, function (t) {
            var player = Bukkit.getPlayer("Glom_");
            cal_velocity(player);
        })
    );
}

function velocityKey(player) {
    return player.name + "-linear-velocity";
}

function fromLocKey(player) {
    return player.name + "-from";
}

function cal_velocity(player) {
    var to = player.location.direction;
    var from = Data.get(fromLocKey(player));
    if (from == null) {
        Data.put(fromLocKey(player), to);
        return;
    }
    if (!from.equals(to)) {
        var timeDifference = 1 / 5;
        var angularVelocity = to.clone().subtract(from);

        if (
            angularVelocity.x == 0 ||
            angularVelocity.y == 0 ||
            angularVelocity.z == 0
        ) {
            Data.remove(velocityKey(player));
            return;
        }
        // 计算线速度
        var linearVelocity = angularVelocity.normalize().multiply(timeDifference);

        Data.put(velocityKey(player), linearVelocity);
    } else {
        Data.remove(velocityKey(player));
    }
    Data.put(fromLocKey(player), to);
}

//@Listener(-event org.bukkit.event.entity.ProjectileLaunchEvent)
function projectileLaunch(event) {
    var config = conf();
    if (!conf().enable) {
        return;
    }
    var projectile = event.getEntity();
    var shooter = projectile.getShooter();
    if (!(shooter instanceof org.bukkit.entity.Player) || shooter == null) return;
    var linearVelocity = Data.get(velocityKey(shooter));
    if (linearVelocity == null) return;
    projectile.velocity = projectile.velocity
        .clone()
        .normalize()
        .add(linearVelocity.multiply(config.inertia.coefficient))
        .multiply(projectile.velocity.length() + linearVelocity.length());
}
