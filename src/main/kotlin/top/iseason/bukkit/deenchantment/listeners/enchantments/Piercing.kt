package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.projectiles.ProjectileSource
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments

//反弹
object Piercing : BaseEnchant(DeEnchantments.DE_piercing) {
    @EventHandler
    fun onProjectileHitEvent(event: ProjectileHitEvent) {
        val entity = event.entity
        val hitEntity = event.hitEntity ?: return
        val shooter = entity.shooter ?: return
        if (shooter !is LivingEntity) return
        if (hitEntity !is ProjectileSource) return
        val equipment = shooter.equipment ?: return
        val level = equipment.itemInMainHand.enchantments[DeEnchantments.DE_piercing]
            ?: equipment.itemInOffHand.enchantments[DeEnchantments.DE_piercing] ?: return
        if (level <= 0) return
        hitEntity.launchProjectile(
            entity.javaClass, entity.velocity.normalize().multiply(-level)
        ).fireTicks = entity.fireTicks
        entity.remove()

    }
}