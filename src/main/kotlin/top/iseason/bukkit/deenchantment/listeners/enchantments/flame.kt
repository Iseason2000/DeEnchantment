package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityShootBowEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments

//神速
object flame : BaseEnchant(DeEnchantments.DE_flame) {
    @EventHandler(priority = EventPriority.LOW)
    fun onEntityShootBowEvent(event: EntityShootBowEvent) {
        if (event.isCancelled) return
        val level = event.bow?.enchantments?.get(DeEnchantments.DE_flame) ?: return
        if (level <= 0) return
        val projectile = event.projectile
        projectile.velocity = projectile.velocity.multiply(level * 0.5 + 1)

    }
}