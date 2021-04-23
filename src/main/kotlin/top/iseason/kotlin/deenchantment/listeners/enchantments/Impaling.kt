package top.iseason.kotlin.deenchantment.listeners.enchantments

import org.bukkit.entity.Guardian
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Trident
import org.bukkit.entity.WaterMob
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import top.iseason.kotlin.deenchantment.manager.DeEnchantment

//刺穿
class Impaling : Listener {
    @EventHandler
    fun onEntityDamageByEntityEvent(event: EntityDamageByEntityEvent) {
        val entity = event.entity
        val damager = event.damager
        if (damager is Trident) {
            if (entity is WaterMob || entity is Guardian) return
            val level = damager.item.enchantments[DeEnchantment.DE_impaling] ?: return
            if (level <= 0) return
            event.damage = event.damage * (1 + 0.25 * level)
        } else {
            if (entity is WaterMob || entity is Guardian) return
            if (damager !is LivingEntity) return
            val level = damager.equipment?.itemInMainHand?.enchantments?.get(DeEnchantment.DE_impaling) ?: return
            if (level <= 0) return
            event.damage = event.damage * (1 + 0.25 * level)
        }
    }
}