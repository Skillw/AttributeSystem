Player = find("org.bukkit.entity.Player");
Config = static("ASConfig").INSTANCE;

function getMythicMobs() {
    if (typeof mm == "undefined") {
        //如没有则赋值
        mm = findMythicInstance();
    }
    return mm;
}

function findMythicInstance() {
    if (Config.mythicMobsIV) {
        return find("io.lumine.xikage.mythicmob.MythicMobs");
    }
    if (Config.mythicMobsV) {
        return find("io.lumine.mythic.bukkit.MythicBukkit");
    }
}

// 释放MM技能
//@Mechanic(mythicskill)
function mythicSkill(data, context, damageType) {
    const enable = data.handle(context.get("enable"));
    if (enable.toString() != "true") {
        return 0.0;
    }
    const skill = data.handle(context.get("skill"));
    const attacker = data.attacker;
    const defender = data.defender;
    taskAsync(function () {
        getMythicMobs()
            .inst()
            .getAPIHelper()
            .castSkill(
                attacker,
                skill,
                attacker,
                attacker.location,
                listOf(defender),
                null,
                1
            );
    })
    return skill;
}

// function getMythicMobsUtil() {
//     if (typeof mmUtil == "undefined") {
//         //如没有则赋值
//         mmUtil = findMythicUtil()
//     }
//     return mmUtil
// }
//
// function findMythicUtil() {
//     if (Config.mythicMobsIV) {
//         return find("io.lumine.xikage.mythicmobs.util.MythicUtil")
//     }
//     if (Config.mythicMobsV) {
//         return find("io.lumine.mythic.core.utils.MythicUtil")
//     }
// }
