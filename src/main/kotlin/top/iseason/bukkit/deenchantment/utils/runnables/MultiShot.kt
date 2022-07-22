package top.iseason.bukkit.deenchantment.utils.runnables

import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Projectile
import org.bukkit.projectiles.ProjectileSource
import org.bukkit.scheduler.BukkitRunnable

class MultiShot(
    val entity: ProjectileSource,
    val level: Int,
    private val projectile: Projectile,
) : BukkitRunnable() {
    override fun run() {
        val speed = projectile.velocity.length()
        val fireTicks = projectile.fireTicks
        val direction = (entity as LivingEntity).location.direction.multiply(speed)
        var angle = 0.0
        for (n in 1..level) {
            angle += 0.2
            val tempAngle = if (n % 2 == 0) -angle else angle
            val abstractArrow = entity.launchProjectile(
                projectile.javaClass,
                direction.rotateAroundY(tempAngle)
            ) as AbstractArrow
            abstractArrow.pickupStatus = AbstractArrow.PickupStatus.CREATIVE_ONLY
            if (fireTicks > 0)
                abstractArrow.fireTicks = fireTicks
        }
    }
}