package top.iseason.kotlin.deenchantment.listeners.enchantments

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Trident
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent
import top.iseason.kotlin.deenchantment.manager.DeEnchantment

//避雷针
class Channeling : Listener {
    @EventHandler
    fun onProjectileHitEvent(event: ProjectileHitEvent) {
        val entity = event.entity
        if (event.hitEntity == null) return
        if (!entity.world.isThundering) return
        if (entity !is Trident) return
        val shooter = entity.shooter
        if (shooter !is LivingEntity) return
        val level = entity.item.enchantments[DeEnchantment.DE_channeling] ?: return
        if (level <= 0) return
        shooter.world.strikeLightning(shooter.location)
    }
}