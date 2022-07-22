package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantment

class Punch : BaseEnchant(DeEnchantment.DE_punch) {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onEntityDamageByEntityEvent(event: EntityDamageByEntityEvent) {
        if (event.isCancelled) return
        //实体判断
        val entity = event.entity
        if (entity !is LivingEntity) return
        val damager = event.damager
        if (damager !is Projectile) return
        val shooter = damager.shooter
        if (shooter !is LivingEntity) return
        //
        val item = shooter.equipment?.itemInMainHand ?: return
        val level = item.enchantments[DeEnchantment.DE_punch] ?: return
        if (level <= 0) return
        val direction = damager.velocity.normalize().multiply(-1)
        entity.velocity = direction.multiply(level * 0.8)
    }
}