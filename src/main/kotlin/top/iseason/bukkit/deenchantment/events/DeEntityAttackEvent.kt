package top.iseason.bukkit.deenchantment.events

import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import top.iseason.bukkit.deenchantment.manager.DeEnchantmentWrapper

class DeEntityAttackEvent(entity: LivingEntity, val event: EntityDamageByEntityEvent) :
    DeEnchantmentEvent(entity, false) {

    private var deEnchantments: MutableMap<DeEnchantmentWrapper, Int>? = null

    override fun getDeEnchantLevel(en: DeEnchantmentWrapper): Int {
        if (deEnchantments == null) {
            deEnchantments = mutableMapOf()
            entity.equipment?.itemInMainHand?.enchantments?.forEach { (t, u) ->
                if (t !is DeEnchantmentWrapper) return@forEach
                deEnchantments!![t] = u
            }
        }
        return deEnchantments!![en] ?: 0
    }
}
