package top.iseason.bukkit.deenchantment.events

import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.inventory.ItemStack
import top.iseason.bukkit.deenchantment.manager.DeEnchantmentWrapper

class DeEntityShootBowEvent(
    entity: LivingEntity,
    val bow: ItemStack?,
    val projectile: Entity,
    val event: EntityShootBowEvent
) : DeEnchantmentEvent(entity, false) {
    override fun setCancelled(cancel: Boolean) {
        super.setCancelled(cancel)
        event.isCancelled = cancel
    }

    override fun getDeEnchantLevel(en: DeEnchantmentWrapper): Int {
        if (deEnchantments == null) {
            deEnchantments = mutableMapOf()
            bow?.enchantments?.forEach { (t, u) ->
                if (t !is DeEnchantmentWrapper) return@forEach
                deEnchantments!![t] = u
            }
        }
        return deEnchantments!![en] ?: 0
    }
}