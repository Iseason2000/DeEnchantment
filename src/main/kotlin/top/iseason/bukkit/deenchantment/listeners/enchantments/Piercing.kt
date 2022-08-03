package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.Entity
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.projectiles.ProjectileSource
import top.iseason.bukkit.bukkittemplate.config.annotations.Comment
import top.iseason.bukkit.bukkittemplate.config.annotations.Key
import top.iseason.bukkit.deenchantment.events.DeEntityShootBowEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments

//反弹
object Piercing : BaseEnchant(DeEnchantments.DE_piercing) {
    @Key
    @Comment("", "反弹速度百分比等级乘数")
    var reboundRate = 0.25

    @Key
    @Comment("", "是否允许等级衰减，true时反弹次数=负魔等级，false 时可无限反弹")
    var allowReduce = true

    private val projectiles = mutableMapOf<Entity, Int>()

    @EventHandler
    fun onDeEntityShootBow(event: DeEntityShootBowEvent) {
        val deLevel = event.getDeLevel()
        if (deLevel != 0) {
            projectiles[event.projectile] = deLevel
        }
    }

    @EventHandler
    fun onProjectileHitEvent(event: ProjectileHitEvent) {
        val entity = event.entity
        val level = projectiles[entity] ?: return
        val hitEntity = event.hitEntity as? ProjectileSource ?: return
        if (level <= 0) return
        val launchProjectile = hitEntity.launchProjectile(
            entity.javaClass, entity.velocity.normalize().multiply(-1 * reboundRate * level)
        )
        val newLevel = if (allowReduce) level - 1 else level
        if (newLevel > 0) {
            projectiles[launchProjectile] = newLevel
        }
        if (entity is AbstractArrow) {
            (launchProjectile as AbstractArrow).pickupStatus = entity.pickupStatus
        }
        launchProjectile.shooter = hitEntity
        launchProjectile.fireTicks = entity.fireTicks
        projectiles.remove(entity)
        entity.remove()
    }
}