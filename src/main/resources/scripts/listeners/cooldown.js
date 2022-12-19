//@Listener(-event com.sucy.skill.api.event.PlayerCastSkillEvent -bind com.sucy.skill.api.event.PlayerCastSkillEvent)
function cooldown(event) {
    const origin = event.skill.cooldown
    event.skill.refreshCooldown()
    event.skill.addCooldown(
        AttributeSystem.formulaManager.calculate(event.player, "skill-cooldown", mapOf({"{cooldown}": origin.toString()}))
    )
}