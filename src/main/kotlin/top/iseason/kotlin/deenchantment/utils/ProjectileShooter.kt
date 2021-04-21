package top.iseason.kotlin.deenchantment.utils

import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.scheduler.BukkitRunnable

class ProjectileShooter(
    val entity: LivingEntity,
    private val projectile: Projectile,
    private var count: Int,
    private val isConsumable: Boolean,
    private val consumeType: Material?
) : BukkitRunnable() {
    private val speed = projectile.velocity.clone().length()
    override fun run() {
        if (isConsumable && consumeType != null) {
            val contents = (entity as Player).inventory.contents
            var num = -1
            var isEnough = false
            for (content in contents) {
                num++
                if (content == null || content.type != consumeType) continue
                isEnough = true
                val amount = content.amount
                if (amount > 1) {
                    content.amount = amount - 1
                    break
                }
                if (amount == 1) {
                    entity.inventory.clear(num)
                    break
                }
            }
            if (!isEnough) {
                this.cancel()
                return
            }
        }
        entity.launchProjectile(projectile.javaClass, entity.location.direction.multiply(speed))
        count--
        if (count <= 0) this.cancel()
    }
}