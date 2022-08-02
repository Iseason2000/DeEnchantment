package top.iseason.bukkit.deenchantment.events

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityDamageByEntityEvent
import top.iseason.bukkit.deenchantment.manager.DeEnchantmentWrapper

class DeEntityProjectileEvent(
    val projectile: Projectile,
    val attacker: LivingEntity,
    val event: EntityDamageByEntityEvent
) : DeEnchantmentEvent(attacker, false) {
    override fun setCancelled(cancel: Boolean) {
        super.setCancelled(cancel)
        event.isCancelled = cancel
    }

    override fun getDeEnchantLevel(en: DeEnchantmentWrapper): Int {
        if (deEnchantments == null) {
            deEnchantments = mutableMapOf()
            attacker.equipment?.itemInMainHand?.enchantments?.forEach { (t, u) ->
                if (t !is DeEnchantmentWrapper) return@forEach
                deEnchantments!![t] = u
            }
        }
        return deEnchantments!![en] ?: 0
    }
}