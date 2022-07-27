package top.iseason.bukkit.deenchantment.events

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityDamageByEntityEvent

class DeEntityProjectileEvent(
    val projectile: Projectile,
    val attacker: LivingEntity,
    val event: EntityDamageByEntityEvent
) :
    DeEnchantmentEvent(attacker, false)