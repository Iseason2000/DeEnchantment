package top.iseason.bukkit.deenchantment.runnables

import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Projectile
import org.bukkit.util.Vector
import top.iseason.bukkit.bukkittemplate.utils.bukkit.getNormalY
import top.iseason.bukkit.bukkittemplate.utils.bukkit.getNormalZ
import java.util.*

class MultiShot(
    val entity: LivingEntity,
    val level: Int,
    private val projectile: Projectile,
    private val angle: Double
) {
    private val normalY: Vector
    private val direction: Vector
    private val speed = projectile.velocity.length()

    init {
        val eyeLocation = entity.eyeLocation
        normalY = eyeLocation.getNormalY()
        direction = eyeLocation.getNormalZ().multiply(speed)
    }

    fun sweepType() {
        val fireTicks = projectile.fireTicks
        val direction = direction.clone()
        var an = 0.0
        for (n in 1..level) {
            an += angle
            val tempAngle = if (n % 2 == 0) -an else an
            val projectile = entity.launchProjectile(
                projectile.javaClass,
                direction.rotateAroundAxis(normalY, tempAngle)
            )
            if (projectile is AbstractArrow) {
                projectile.pickupStatus = AbstractArrow.PickupStatus.CREATIVE_ONLY
                if (fireTicks > 0)
                    projectile.fireTicks = fireTicks
            }
        }
    }

    fun randomType() {
        val fireTicks = projectile.fireTicks
        val random = Random()
        for (n in 1..level) {
            val direction = direction.clone()
            direction.rotateAroundX((random.nextDouble() - 0.5) * angle * 2)
            direction.rotateAroundY((random.nextDouble() - 0.5) * angle * 2)
            direction.rotateAroundZ((random.nextDouble() - 0.5) * angle * 2)
            val projectile = entity.launchProjectile(
                projectile.javaClass,
                direction
            )
            if (projectile is AbstractArrow) {
                projectile.pickupStatus = AbstractArrow.PickupStatus.CREATIVE_ONLY
                if (fireTicks > 0)
                    projectile.fireTicks = fireTicks
            }
        }
    }
}