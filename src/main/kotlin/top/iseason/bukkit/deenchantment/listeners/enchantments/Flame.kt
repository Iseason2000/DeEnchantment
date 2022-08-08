package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import top.iseason.bukkit.bukkittemplate.config.annotations.Comment
import top.iseason.bukkit.bukkittemplate.config.annotations.Key
import top.iseason.bukkit.deenchantment.events.DeEntityShootBowEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments

//神速
object Flame : BaseEnchant(DeEnchantments.DE_flame) {
    @Key
    @Comment("", "速度倍率等级乘数")
    var speedRate = 0.2

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onEntityShootBowEvent(event: DeEntityShootBowEvent) {
        val level = event.getDeLevel()
        if (level <= 0) return
        if (!checkPermission(event.entity as? Player)) return
        val projectile = event.projectile
        projectile.velocity = projectile.velocity.multiply(level * speedRate + 1)
    }
}