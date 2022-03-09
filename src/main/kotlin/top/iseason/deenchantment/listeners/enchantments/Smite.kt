package top.iseason.deenchantment.listeners.enchantments

import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import top.iseason.deenchantment.manager.DeEnchantment
import top.iseason.deenchantment.utils.EntityTools

//亡灵救赎
class Smite : Listener {
    @EventHandler
    fun onEntityDamageByEntityEvent(event: EntityDamageByEntityEvent) {
        if (event.isCancelled) return
        val entity = event.entity
        if (entity !is LivingEntity) return
        if (!EntityTools.isUndead(entity)) return
        val damager = event.damager
        if (damager !is LivingEntity) return
        val item = damager.equipment?.itemInMainHand ?: return
        val level = item.enchantments[DeEnchantment.DE_smite] ?: return
        if (level <= 0) return
        val maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: return
        val damage = event.damage - 2.5 * level
        if (entity.health >= maxHealth && damage < 0) {
            event.damage = 0.0
            return
        }
        event.damage = event.damage - 2.5 * level
    }
}