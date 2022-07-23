package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkit.deenchantment.utils.Tools

//横扫失败
object Sweeping : BaseEnchant(DeEnchantments.DE_sweeping) {
    @EventHandler
    fun onEntityDamageByEntityEvent(event: EntityDamageByEntityEvent) {
        if (event.isCancelled) return
        if (event.cause != EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) return
        val damager = event.damager
        if (damager !is Player) return
        val level = damager.inventory.itemInMainHand.enchantments[DeEnchantments.DE_sweeping] ?: return
        if (level <= 0) return
        if (Tools.getRandomDouble() >= level * 0.1) return
        event.isCancelled = true
    }
}