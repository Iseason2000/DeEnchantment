package top.iseason.kotlin.deenchantment.utils

import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Projectile
import org.bukkit.projectiles.ProjectileSource
import org.bukkit.scheduler.BukkitRunnable

@Deprecated("支持n重，但是由于性能考虑移除了")
class MultiShot(
    val entity: ProjectileSource,
    val level: Int,
    val projectile: Projectile,
) : BukkitRunnable() {
    override fun run() {
        val speed = projectile.velocity.length()
        val fireTicks = projectile.fireTicks
        val direction = (entity as LivingEntity).location.direction
        var angle = 0.0
        for (n in 1..level) {
            angle += 0.2
            val tempAngle = if (n % 2 == 0) -angle else angle
            val abstractArrow = entity.launchProjectile(
                projectile.javaClass,
                direction.rotateAroundY(tempAngle).multiply(speed)
            ) as AbstractArrow
            abstractArrow.pickupStatus = AbstractArrow.PickupStatus.CREATIVE_ONLY
            if (fireTicks > 0)
                abstractArrow.fireTicks = fireTicks
        }
    }

}