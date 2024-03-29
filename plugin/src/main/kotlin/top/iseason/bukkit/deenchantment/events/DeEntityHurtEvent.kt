package top.iseason.bukkit.deenchantment.events

import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageEvent

class DeEntityHurtEvent(entity: LivingEntity, val event: EntityDamageEvent) : DeEnchantmentEvent(entity, false) {
    override fun setCancelled(cancel: Boolean) {
        super.setCancelled(cancel)
        event.isCancelled = cancel
    }
}