package top.iseason.kotlin.deenchantment.listeners.enchantments

import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import top.iseason.kotlin.deenchantment.manager.DeEnchantment

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
        val maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: return
        if (entity.health == maxHealth) {
            event.damage = 0.0
            return
        }
        event.damage = event.damage - 0.5 * level
    }
}