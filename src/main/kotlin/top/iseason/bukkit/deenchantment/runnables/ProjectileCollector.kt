package top.iseason.bukkit.deenchantment.runnables

import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.scheduler.BukkitRunnable

class ProjectileCollector(val player: Player, val radius: Double) : BukkitRunnable() {
    override fun run() {
        val location = player.eyeLocation
        for (nearbyEntity in player.getNearbyEntities(radius, radius, radius)) {
            if (nearbyEntity !is Projectile) continue
            if (nearbyEntity.isOnGround) continue
            nearbyEntity.velocity = location.toVector().subtract(nearbyEntity.location.toVector()).normalize()
                .multiply(nearbyEntity.velocity.length())
        }

    }
}