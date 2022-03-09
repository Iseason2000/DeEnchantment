package top.iseason.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemDamageEvent
import top.iseason.deenchantment.manager.DeEnchantment

//易损
class UnBreaking : Listener {
    @EventHandler
    fun onPlayerItemDamageEvent(event: PlayerItemDamageEvent) {
        if (event.isCancelled) return
        val level = event.item.enchantments[DeEnchantment.DE_unbreaking] ?: return
        if (level <= 0) return
        event.damage = event.damage + level
    }
}