package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkit.deenchantment.utils.Tools

//引火烧身
object Fire_Aspect : BaseEnchant(DeEnchantments.DE_fire_aspect) {
    @EventHandler
    fun onEntityDamageByEntityEvent(event: EntityDamageByEntityEvent) {
        if (event.isCancelled) return
        val damager = event.damager
        if (damager !is LivingEntity) return
        val item = damager.equipment?.itemInMainHand ?: return
        val level = item.enchantments[DeEnchantments.DE_fire_aspect] ?: return
        if (level <= 0) return
        if (Tools.getRandomDouble() >= level * 0.2) return
        damager.fireTicks = level * 100
    }
}