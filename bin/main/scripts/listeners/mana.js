Player = find("org.bukkit.entity.Player");


//@Listener(-event com.skillw.attsystem.api.event.AttributeUpdateEvent -bind com.sucy.skill.api.event.PlayerManaGainEvent)
function maxMana(event) {
    SkillAPI = find("com.sucy.skill.SkillAPI");
    const entity = event.entity;
    if (entity instanceof Player && event.getTime().name().equals("AFTER")) {
        const maxMana = AttributeSystem.formulaManager.get(entity.uniqueId, "max-mana");
        const skillAPI = SkillAPI.getPlayerData(entity);
        skillAPI.addMaxMana(maxMana - skillAPI.getMaxMana());
    }
}

//@Listener(-event com.sucy.skill.api.event.PlayerManaGainEvent -bind com.sucy.skill.api.event.PlayerManaGainEvent)
function manaRegain(event) {
    const mana = AttributeSystem.formulaManager.get(event.playerData.player, "mana-regain");
    event.setAmount(mana + event.getAmount())
}


