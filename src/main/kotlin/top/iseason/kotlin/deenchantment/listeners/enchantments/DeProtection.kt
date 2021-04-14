package top.iseason.kotlin.deenchantment.listeners.enchantments

import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import top.iseason.kotlin.deenchantment.manager.DeEnchantment

//保护不了 附魔 实现
class DeProtection : Listener {
    @EventHandler
    fun onEntityDamageEvent(event: EntityDamageEvent) {
        if (event.isCancelled) return
        val entity = event.entity
        if (entity !is LivingEntity) return
        val equipments = entity.equipment?.armorContents ?: return
        var levelCount = 0
        for (equipment in equipments)
            levelCount += equipment?.enchantments?.get(DeEnchantment.DE_protection) ?: continue
        val rawDamage = event.getDamage(EntityDamageEvent.DamageModifier.MAGIC)
        val finalDamage = rawDamage + event.finalDamage * (levelCount * 4 * 0.01)
        event.setDamage(EntityDamageEvent.DamageModifier.MAGIC, finalDamage)
    }
}