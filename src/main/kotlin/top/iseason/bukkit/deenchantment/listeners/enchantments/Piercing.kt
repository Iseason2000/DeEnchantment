package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.ThrowableProjectile
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.projectiles.ProjectileSource
import top.iseason.bukkit.bukkittemplate.config.annotations.Comment
import top.iseason.bukkit.bukkittemplate.config.annotations.Key
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments

//反弹
object Piercing : BaseEnchant(DeEnchantments.DE_piercing) {
    @Key
    @Comment("", "反弹速度百分比等级乘数")
    var reboundRate = 0.25

    @EventHandler
    fun onProjectileHitEvent(event: ProjectileHitEvent) {
        val entity = event.entity as? ThrowableProjectile ?: return
        val hitEntity = event.hitEntity as? ProjectileSource ?: return
        val shooter = entity.shooter as? LivingEntity ?: return
        val level = entity.item.getDeLevel()
        if (level <= 0) return
        val launchProjectile = hitEntity.launchProjectile(
            entity.javaClass, entity.velocity.normalize().multiply(-1 * reboundRate * level)
        )
        launchProjectile.shooter = shooter
        launchProjectile.item = entity.item
        launchProjectile.fireTicks = entity.fireTicks
        entity.remove()
    }
}