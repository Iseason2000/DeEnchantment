package top.iseason.deenchantment.listeners.enchantments

import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import top.iseason.deenchantment.manager.DeEnchantment

//磨钝
class Sharpness : Listener {

    @EventHandler
    fun onEntityDamageByEntityEvent(event: EntityDamageByEntityEvent) {
        if (event.isCancelled) return
        val damager = event.damager
        if (damager !is LivingEntity) return
        val entity = event.entity
        if (entity !is LivingEntity) return
        val item = damager.equipment?.itemInMainHand ?: return
        val level = item.enchantments[DeEnchantment.DE_sharpness] ?: return
        if (level <= 0) return
        var damage = event.damage - 0.5 * level
        if (damage < 0) damage = 0.0
        event.damage = damage
    }
}