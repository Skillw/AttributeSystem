//@Listener(-event org.bukkit.event.player.PlayerExpChangeEvent)
function exp(event) {
    const player = event.player;
    const data = AttrAPI.getAttrData(player);
    if (data == null) return
    const value = data.getAttrValue("ExpAddition", "value");
    const percent = data.getAttrValue("ExpAddition", "percent");
    event.amount = (event.amount + value) * (1 + percent);
}
