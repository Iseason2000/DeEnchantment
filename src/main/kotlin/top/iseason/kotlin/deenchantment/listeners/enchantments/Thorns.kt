package top.iseason.kotlin.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import top.iseason.kotlin.deenchantment.manager.DeEnchantment
import top.iseason.kotlin.deenchantment.utils.EnchantTools
import top.iseason.kotlin.deenchantment.utils.Tools

//负荆请罪
class Thorns : Listener {
    @EventHandler
    fun onPlayerMoveEvent(event: PlayerMoveEvent) {
        if (event.isCancelled) return
        val player = event.player
        val levelCount = EnchantTools.getLevelCount(player, DeEnchantment.DE_thorns)
        if (levelCount == 0) return
        if (Tools.getRandomDouble() < levelCount * 0.005) {
            player.damage(0.5 * levelCount)
        }
    }
}