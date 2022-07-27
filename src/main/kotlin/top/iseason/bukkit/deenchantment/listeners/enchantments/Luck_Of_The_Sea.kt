package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerFishEvent
import top.iseason.bukkit.bukkittemplate.utils.sendColorMessage
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkit.deenchantment.utils.Tools

//海之嫌弃
object Luck_Of_The_Sea : BaseEnchant(DeEnchantments.DE_luck_of_the_sea) {
    @EventHandler
    fun onPlayerFishEvent(event: PlayerFishEvent) {
        if (event.isCancelled) return
        if (event.state != PlayerFishEvent.State.CAUGHT_FISH) return
        val player = event.player
        val equipment = player.equipment ?: return
        val level = equipment.itemInMainHand.enchantments[DeEnchantments.DE_luck_of_the_sea]
            ?: equipment.itemInOffHand.enchantments[DeEnchantments.DE_luck_of_the_sea] ?: return
        if (level <= 0) return
        if (Tools.getRandomDouble() > level * 0.1) return
        event.caught?.remove() ?: return
        player.sendColorMessage("${ChatColor.YELLOW}你受到了大海的嫌弃并回收了你的东西")
    }
}