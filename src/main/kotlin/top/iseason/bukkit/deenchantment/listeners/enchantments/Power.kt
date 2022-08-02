package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.AbstractArrow
import org.bukkit.event.EventHandler
import top.iseason.bukkit.bukkittemplate.config.annotations.Comment
import top.iseason.bukkit.bukkittemplate.config.annotations.Key
import top.iseason.bukkit.deenchantment.events.DeEntityShootBowEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments

//虚弱
object Power : BaseEnchant(DeEnchantments.DE_power) {
    @Key
    @Comment("", "伤害及速度百分比等级乘数")
    var damageRate = 0.15

    @EventHandler(ignoreCancelled = true)
    fun onEntityDamageByEntityEvent(event: DeEntityShootBowEvent) {
        val level = event.getDeLevel()
        if (level <= 0) return
        val projectile = event.projectile
        if (projectile !is AbstractArrow) return
        var percentage = damageRate * level
        if (percentage >= 1.0) percentage = 1.0
        projectile.damage = projectile.damage * (1 - percentage)
        projectile.velocity = projectile.velocity.multiply(1 - percentage)

    }

}