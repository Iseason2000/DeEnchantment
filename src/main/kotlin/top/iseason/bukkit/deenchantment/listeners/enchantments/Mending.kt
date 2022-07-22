package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerItemDamageEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantment

class Mending : BaseEnchant(DeEnchantment.DE_mending) {
    @EventHandler
    fun onPlayerItemDamageEvent(event: PlayerItemDamageEvent) {
        if (event.isCancelled) return
        val level = event.item.enchantments[DeEnchantment.DE_mending] ?: return
        if (level <= 0) return
        if (event.damage < 1) return
        val player = event.player
        player.giveExp(level)
    }
}