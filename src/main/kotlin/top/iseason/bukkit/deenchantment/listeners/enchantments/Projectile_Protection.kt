package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import top.iseason.bukkit.bukkittemplate.utils.submit
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkit.deenchantment.runnables.TargetFinder

//弹射物吸引 TODO: 重构
object Projectile_Protection : BaseEnchant(DeEnchantments.DE_projectile_protection) {

    @EventHandler
    fun onProjectileLaunchEvent(event: ProjectileLaunchEvent) {
        if (event.isCancelled) return
        //超过128个任务则不会创建，防止卡顿
        if (!TargetFinder.isMaxPool())
            submit(8, 2, task = TargetFinder(event.entity))
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
//        val levelCount = EnchantTools.getLevelCount(entity, DeEnchantments.DE_projectile_protection)
//        if (levelCount == 0) return
//        event.damage = event.damage + event.damage * (levelCount * 8 * 0.01)
//    }

}