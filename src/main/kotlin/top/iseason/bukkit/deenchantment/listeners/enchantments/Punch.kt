package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import top.iseason.bukkit.bukkittemplate.config.annotations.Comment
import top.iseason.bukkit.bukkittemplate.config.annotations.Key
import top.iseason.bukkit.deenchantment.events.DeEntityProjectileEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments

object Punch : BaseEnchant(DeEnchantments.DE_punch) {

    @Key
    @Comment("", "吸引速度等级乘数")
    var velocityRate = 0.8

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onEntityDamageByEntityEvent(event: DeEntityProjectileEvent) {
        val level = event.getDeLevel()
        if (level <= 0) return
        val attacker = event.attacker
        val direction = attacker.velocity.normalize().multiply(-1)
        event.event.entity.velocity = direction.multiply(level * velocityRate)
    }
}