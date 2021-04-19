package top.iseason.kotlin.deenchantment.listeners.enchantments

import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import top.iseason.kotlin.deenchantment.manager.DeEnchantment

//退击
class KnockBack : Listener {
    @EventHandler//todo:待检验
    fun onEntityDamageByEntityEvent(event: EntityDamageByEntityEvent) {
        if (event.isCancelled) return
        val damager = event.damager
        val entity = event.entity
        if (damager !is LivingEntity) return
        if (entity !is LivingEntity) return
        val item = damager.equipment?.itemInMainHand ?: return
        val level = item.enchantments[DeEnchantment.DE_knockback] ?: return
        if (level <= 0) return
        val direction = damager.location.direction.normalize().multiply(-1)
        damager.velocity = damager.velocity.add(direction.multiply(level * 0.6))


    }
}