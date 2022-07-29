package top.iseason.bukkit.deenchantment.listeners.triggers

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.player.PlayerFishEvent
import top.iseason.bukkit.deenchantment.events.*

object EntityDeEnchantCaller : Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onPlayerAttack(event: EntityDamageByEntityEvent) {
        val damager = event.damager
        if (damager is LivingEntity) {
            DeEntityAttackEvent(damager, event).call()
        } else if (damager is Projectile) {
            val shooter = damager.shooter as? LivingEntity ?: return
            DeEntityProjectileEvent(damager, shooter, event).call()
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onPlayerHurt(event: EntityDamageEvent) {
        val entity = event.entity
        if (entity is LivingEntity) {
            DeEntityHurtEvent(entity, event).call()
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onPlayerBreak(event: BlockBreakEvent) {
        DeBreakBlockEvent(event.player, event).call()
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onEntityShootBow(event: EntityShootBowEvent) {
        DeEntityShootBowEvent(event.entity, event.bow, event.projectile, event).call()
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onPlayerFish(event: PlayerFishEvent) {
        DePlayerFishEvent(event.player, event).call()
    }

}