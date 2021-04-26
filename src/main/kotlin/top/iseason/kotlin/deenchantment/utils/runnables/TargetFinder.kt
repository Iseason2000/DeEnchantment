package top.iseason.kotlin.deenchantment.utils.runnables

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Projectile
import org.bukkit.scheduler.BukkitRunnable
import top.iseason.kotlin.deenchantment.manager.DeEnchantment
import top.iseason.kotlin.deenchantment.utils.EnchantTools

//弹射物追踪
class TargetFinder(
    private val projectile: Projectile
) : BukkitRunnable() {
    private var isFind = false
    override fun run() {
        //仅转换一次，成功则取消事件
        if (isFind || projectile.isDead || projectile.isOnGround) {
            this.cancel()
            return
        }
        val nearbyEntities = projectile.getNearbyEntities(16.0, 16.0, 16.0)//寻找范围
        if (nearbyEntities.isEmpty()) return
        val location = projectile.location.clone()
        nearbyEntities.sortBy {
            it.location.distance(location)
        }
        for (entity in nearbyEntities) {
            if (entity !is LivingEntity) continue
            if (entity == projectile.shooter) continue
            val levelCount = EnchantTools.getLevelCount(entity, DeEnchantment.DE_projectile_protection)
            if (levelCount == 0) continue
            if (levelCount < location.distance(entity.location) - 1) continue
            isFind = true
            projectile.setGravity(false)
            val attract = entity.eyeLocation.toVector()
                .subtract(location.toVector()).normalize()
            projectile.velocity = attract.multiply(projectile.velocity.length())
            return
        }
    }
}