package top.iseason.kotlin.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemDamageEvent
import top.iseason.kotlin.deenchantment.manager.DeEnchantment

class Mending : Listener {
    @EventHandler
    fun onPlayerItemDamageEvent(event: PlayerItemDamageEvent) {
        if (event.isCancelled) return
        val level = event.item.enchantments[DeEnchantment.DE_mending] ?: return
        if (level <= 0) return
        val player = event.player
        player.giveExp(level)
    }
}