package top.iseason.bukkit.deenchantment.listeners.triggers

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import top.iseason.bukkit.deenchantment.events.DeEntityAttackEvent
import top.iseason.bukkit.deenchantment.events.DeEntityHurtEvent
import top.iseason.bukkit.deenchantment.events.DeEntityProjectileEvent
import top.iseason.bukkit.deenchantment.events.call

object EntityDeEnchantCaller : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onPlayerAttack(event: EntityDamageByEntityEvent) {
        val damager = event.damager
        if (damager is LivingEntity) {
            DeEntityAttackEvent(damager, event).call()
        } else if (damager is Projectile) {
            val shooter = damager.shooter as? LivingEntity ?: return
            DeEntityProjectileEvent(damager, shooter, event).call()
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerHurt(event: EntityDamageEvent) {
        val entity = event.entity
        if (entity is LivingEntity) {
            DeEntityHurtEvent(entity, event).call()
        }
    }
}