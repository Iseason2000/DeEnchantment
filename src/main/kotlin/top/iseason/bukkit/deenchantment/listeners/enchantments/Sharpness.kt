package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments

//磨钝
object Sharpness : BaseEnchant(DeEnchantments.DE_sharpness) {

    @EventHandler
    fun onEntityDamageByEntityEvent(event: EntityDamageByEntityEvent) {
        if (event.isCancelled) return
        val damager = event.damager
        if (damager !is LivingEntity) return
        val entity = event.entity
        if (entity !is LivingEntity) return
        val item = damager.equipment?.itemInMainHand ?: return
        val level = item.enchantments[DeEnchantments.DE_sharpness] ?: return
        if (level <= 0) return
        var damage = event.damage - 0.5 * level
        if (damage < 0) damage = 0.0
        event.damage = damage
    }
}