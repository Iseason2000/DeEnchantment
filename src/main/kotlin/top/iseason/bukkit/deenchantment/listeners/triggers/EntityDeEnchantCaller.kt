package top.iseason.bukkit.deenchantment.listeners.triggers

import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import top.iseason.bukkit.deenchantment.events.DeEntityHurtEvent
import top.iseason.bukkit.deenchantment.events.DeEntityMainHandEvent
import top.iseason.bukkit.deenchantment.events.call

object EntityDeEnchantCaller : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onPlayerHurt(event: EntityDamageByEntityEvent) {
        val entity = event.entity
        if (entity is LivingEntity) {
            DeEntityHurtEvent(entity, event).call()
        }
        val damager = event.damager
        if (damager is LivingEntity) {
            DeEntityMainHandEvent(damager, event).call()
        }
    }
}