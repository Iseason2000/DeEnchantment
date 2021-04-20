package top.iseason.kotlin.deenchantment.listeners.enchantments

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import top.iseason.kotlin.deenchantment.manager.DeEnchantment
import top.iseason.kotlin.deenchantment.utils.Tools

//横扫失败
class Sweeping : Listener {
    @EventHandler
    fun onEntityDamageByEntityEvent(event: EntityDamageByEntityEvent) {
        if (event.isCancelled) return
        if (event.cause != EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) return
        val damager = event.damager
        if (damager !is Player) return
        val level = damager.inventory.itemInMainHand.enchantments[DeEnchantment.DE_sweeping] ?: return
        if (level <= 0) return
        if (Tools.getRandomDouble() >= level * 0.1) return
        event.isCancelled = true
    }
}