package top.iseason.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityShootBowEvent
import top.iseason.deenchantment.manager.DeEnchantment

//神速
class flame : Listener {
    @EventHandler(priority = EventPriority.LOW)
    fun onEntityShootBowEvent(event: EntityShootBowEvent) {
        if (event.isCancelled) return
        val level = event.bow?.enchantments?.get(DeEnchantment.DE_flame) ?: return
        if (level <= 0) return
        val projectile = event.projectile
        projectile.velocity = projectile.velocity.multiply(level * 0.5 + 1)

    }
}