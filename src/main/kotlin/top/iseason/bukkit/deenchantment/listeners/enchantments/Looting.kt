package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDeathEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkit.deenchantment.utils.Tools

//知足
object Looting : BaseEnchant(DeEnchantments.DE_looting) {
    @EventHandler
    fun onEntityDeathEvent(event: EntityDeathEvent) {
        val killer = event.entity.killer ?: return
        val drops = event.drops
        if (drops.isEmpty()) return
        val level = killer.inventory.itemInMainHand.enchantments[DeEnchantments.DE_looting] ?: return
        if (level <= 0) return
        if (Tools.getRandomDouble() >= level * 0.05) return
        drops.clear()
    }
}