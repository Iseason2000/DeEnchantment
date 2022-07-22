package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerItemDamageEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantment

//易损
class UnBreaking : BaseEnchant(DeEnchantment.DE_unbreaking) {
    @EventHandler
    fun onPlayerItemDamageEvent(event: PlayerItemDamageEvent) {
        if (event.isCancelled) return
        val level = event.item.enchantments[DeEnchantment.DE_unbreaking] ?: return
        if (level <= 0) return
        event.damage = event.damage + level
    }
}