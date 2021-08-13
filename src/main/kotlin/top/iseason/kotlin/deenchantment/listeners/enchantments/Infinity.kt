package top.iseason.kotlin.deenchantment.listeners.enchantments

import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityShootBowEvent
import top.iseason.kotlin.deenchantment.manager.ConfigManager
import top.iseason.kotlin.deenchantment.manager.DeEnchantment
import top.iseason.kotlin.deenchantment.utils.runnables.MultiShot

//多重
class Infinity : Listener {
//    @EventHandler
//    fun onEntityShootBowEvent(event: EntityShootBowEvent) {
//        if (event.isCancelled) return
//        val bow = event.bow ?: return
//        val level = bow.enchantments[DeEnchantment.DE_infinity] ?: return
//        if (level <= 0) return
//        val projectile = event.projectile
//        if (projectile !is Projectile) return
//        val entity = event.entity
//        val speed = projectile.velocity.length()
//        val fireTicks = projectile.fireTicks
//        val direction = entity.location.direction
//        val abstractArrow1 = entity.launchProjectile(
//            projectile.javaClass,
//            direction.rotateAroundY(0.2).multiply(speed)
//        ) as AbstractArrow
//        val abstractArrow2 = entity.launchProjectile(
//            projectile.javaClass,
//            direction.rotateAroundY(-0.4).multiply(speed)
//        ) as AbstractArrow
//        abstractArrow1.pickupStatus = AbstractArrow.PickupStatus.CREATIVE_ONLY
//        abstractArrow2.pickupStatus = AbstractArrow.PickupStatus.CREATIVE_ONLY
//        if (fireTicks > 0) {
//            abstractArrow1.fireTicks = fireTicks
//            abstractArrow2.fireTicks = fireTicks
//        }
//
//    }


    @EventHandler
    fun onEntityShootBowEvent(event: EntityShootBowEvent) {
        if (event.isCancelled) return
        val bow = event.bow ?: return
        val level = bow.enchantments[DeEnchantment.DE_infinity] ?: return
        if (level <= 0) return
        val projectile = event.projectile
        if (projectile !is Projectile) return
        val entity = event.entity
        MultiShot(entity, level, projectile).runTask(ConfigManager.getPlugin())

    }
}