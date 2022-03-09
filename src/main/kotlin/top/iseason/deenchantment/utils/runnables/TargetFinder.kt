package top.iseason.deenchantment.utils.runnables

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Projectile
import org.bukkit.scheduler.BukkitRunnable
import top.iseason.deenchantment.manager.DeEnchantment
import top.iseason.deenchantment.utils.EnchantTools

/**
 * 弹射物追踪
 */
class TargetFinder(private val projectile: Projectile) : BukkitRunnable() {
    companion object {
        private val launchMap = mutableMapOf<Projectile, TargetFinder>()

        fun remove(projectile: Projectile) {
            if (launchMap.containsKey(projectile)) {
                launchMap[projectile]!!.cancel()
                launchMap.remove(projectile)
            }
        }

        /**
         * 是否达超过128个任务
         */
        fun isMaxPool() = launchMap.size > 128

        fun removeAll() {
            launchMap.values.forEach { it.cancel() }
            launchMap.clear()
        }
    }

    private val initTime = System.currentTimeMillis()

    init {
        launchMap[projectile] = this
    }

    override fun run() {
        //仅转换一次，成功则取消事件,超时20秒取消任务
        if (projectile.isDead || projectile.isOnGround || System.currentTimeMillis() - initTime > 10000) {
            remove(projectile)
            return
        }
        //寻找范围,10*10*10
        val nearbyLivingEntities = projectile.getNearbyEntities(5.0, 5.0, 5.0).filterIsInstance<LivingEntity>()
        if (nearbyLivingEntities.isEmpty()) return
        val location = projectile.location.clone()
        val sortedBy = nearbyLivingEntities.sortedBy { it.location.distance(location) }
        for (entity in sortedBy) {
            if (entity == projectile.shooter) continue
            val levelCount = EnchantTools.getLevelCount(entity, DeEnchantment.DE_projectile_protection) * 2
            if (levelCount == 0) continue
            if (levelCount < location.distance(entity.location) - 1) continue
            //找到目标了
            projectile.setGravity(false)
            val attract = entity.eyeLocation.toVector()
                .subtract(location.toVector()).normalize()
            projectile.velocity = attract.multiply(projectile.velocity.length())
            cancel()
            launchMap.remove(projectile)
            return
        }
    }
}