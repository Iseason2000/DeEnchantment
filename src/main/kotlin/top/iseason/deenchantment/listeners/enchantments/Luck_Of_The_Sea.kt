package top.iseason.deenchantment.listeners.enchantments

import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent
import top.iseason.deenchantment.manager.DeEnchantment
import top.iseason.deenchantment.utils.LogSender
import top.iseason.deenchantment.utils.Tools

//海之嫌弃
class Luck_Of_The_Sea : Listener {
    @EventHandler
    fun onPlayerFishEvent(event: PlayerFishEvent) {
        if (event.isCancelled) return
        if (event.state != PlayerFishEvent.State.CAUGHT_FISH) return
        val player = event.player
        val equipment = player.equipment ?: return
        val level = equipment.itemInMainHand.enchantments[DeEnchantment.DE_luck_of_the_sea]
            ?: equipment.itemInOffHand.enchantments[DeEnchantment.DE_luck_of_the_sea] ?: return
        if (level <= 0) return
        if (Tools.getRandomDouble() > level * 0.1) return
        event.caught?.remove() ?: return
        LogSender.log(player, "${ChatColor.YELLOW}你受到了大海的嫌弃并回收了你的东西")
    }
}