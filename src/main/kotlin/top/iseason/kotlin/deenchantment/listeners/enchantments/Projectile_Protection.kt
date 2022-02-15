package top.iseason.kotlin.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import top.iseason.kotlin.deenchantment.manager.ConfigManager
import top.iseason.kotlin.deenchantment.utils.runnables.TargetFinder

//弹射物吸引
class Projectile_Protection : Listener {

    @EventHandler
    fun onProjectileLaunchEvent(event: ProjectileLaunchEvent) {
        TargetFinder(event.entity).runTaskTimer(ConfigManager.getPlugin(), 8, 2)
    }

    @EventHandler
    fun onProjectileHitEvent(event: ProjectileHitEvent) {
        val entity = event.entity
        TargetFinder.remove(entity)
        entity.setGravity(true)
    }

    //增加伤害
//    @EventHandler
//    fun onEntityDamageEvent(event: EntityDamageEvent) {
//        if (event.isCancelled) return
//        if (event.cause != EntityDamageEvent.DamageCause.PROJECTILE) return
//        val entity = event.entity
//        if (entity !is LivingEntity) return
//        val levelCount = EnchantTools.getLevelCount(entity, DeEnchantment.DE_projectile_protection)
//        if (levelCount == 0) return
//        event.damage = event.damage + event.damage * (levelCount * 8 * 0.01)
//    }

}