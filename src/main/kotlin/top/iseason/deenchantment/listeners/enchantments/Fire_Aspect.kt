package top.iseason.deenchantment.listeners.enchantments

import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import top.iseason.deenchantment.manager.DeEnchantment
import top.iseason.deenchantment.utils.Tools

//引火烧身
class Fire_Aspect : Listener {
    @EventHandler
    fun onEntityDamageByEntityEvent(event: EntityDamageByEntityEvent) {
        if (event.isCancelled) return
        val damager = event.damager
        if (damager !is LivingEntity) return
        val item = damager.equipment?.itemInMainHand ?: return
        val level = item.enchantments[DeEnchantment.DE_fire_aspect] ?: return
        if (level <= 0) return
        if (Tools.getRandomDouble() >= level * 0.2) return
        damager.fireTicks = level * 100
    }
}