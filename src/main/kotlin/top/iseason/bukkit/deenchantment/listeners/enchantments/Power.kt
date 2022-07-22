package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.AbstractArrow
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityShootBowEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantment

//虚弱
class Power : BaseEnchant(DeEnchantment.DE_power) {
    @EventHandler
    fun onEntityDamageByEntityEvent(event: EntityShootBowEvent) {
        if (event.isCancelled) return
        val level = event.bow?.enchantments?.get(DeEnchantment.DE_power) ?: return
        if (level <= 0) return
        val projectile = event.projectile
        if (projectile !is AbstractArrow) return
        var percentage = 0.15 * level
        if (percentage >= 1) percentage = 0.9
        projectile.damage = projectile.damage * (1 - percentage)
        projectile.velocity = projectile.velocity.multiply(1 - percentage)

    }


}