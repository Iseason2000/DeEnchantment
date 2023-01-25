package top.iseason.bukkit.deenchantment.runnables

import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.scheduler.BukkitRunnable
import top.iseason.bukkittemplate.utils.bukkit.EntityUtils.takeItem

class ProjectileShooter(
    val entity: LivingEntity,
    private val projectile: Projectile,
    private var count: Int,
    private val isConsumable: Boolean,
    private val consumeType: Material?
) : BukkitRunnable() {
    private val speed = projectile.velocity.clone().length()
    override fun run() {
        if (isConsumable && !(entity as Player).takeItem(consumeType!!, 1)) {
            this.cancel()
            return
        }
        entity.launchProjectile(projectile.javaClass, entity.location.direction.multiply(speed))
        count--
        if (count <= 0) this.cancel()
    }
}