package top.iseason.kotlin.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import top.iseason.kotlin.deenchantment.manager.DeEnchantment
import top.iseason.kotlin.deenchantment.utils.Tools

//知足
class Looting : Listener {
    @EventHandler
    fun onEntityDeathEvent(event: EntityDeathEvent) {
        val killer = event.entity.killer ?: return
        val drops = event.drops
        if (drops.isEmpty()) return
        val level = killer.inventory.itemInMainHand.enchantments[DeEnchantment.DE_looting] ?: return
        if (level <= 0) return
        if (Tools.getRandomDouble() >= level * 0.05) return
        drops.clear()
    }
}