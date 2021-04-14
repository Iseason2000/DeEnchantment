package top.iseason.kotlin.deenchantment.listeners.enchantments

import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityCombustEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import top.iseason.kotlin.deenchantment.manager.DeEnchantment

class DeFireProtection : Listener {
    @EventHandler
    fun onEntityCombustByBlockEvent(event: EntityCombustEvent) {
        if (event.isCancelled) return
        val entity = event.entity
        if (entity !is LivingEntity) return
        val equipments = entity.equipment?.armorContents ?: return
        var maxLevel = 0
        for (equipment in equipments) {
            val level = equipment?.enchantments?.get(DeEnchantment.DE_fire_protection) ?: continue
            if (level > maxLevel) maxLevel = level
        }
        if (maxLevel == 0) return
        val duration = event.duration * ((15 * maxLevel) * 0.01 + 1)
        event.duration = duration.toInt()
    }

    @EventHandler
    fun onEntityDamageEvent(event: EntityDamageEvent) {
        if (event.isCancelled) return
        if (event.cause != DamageCause.FIRE && event.cause != DamageCause.FIRE_TICK) return
        val entity = event.entity
        if (entity !is LivingEntity) return
        val equipments = entity.equipment?.armorContents ?: return
        var levelCount = 0
        for (equipment in equipments) {
            val level = equipment?.enchantments?.get(DeEnchantment.DE_fire_protection) ?: continue
            levelCount += level
        }
        //设置伤害
        val rawDamage = event.getDamage(EntityDamageEvent.DamageModifier.MAGIC)
        val finalDamage = rawDamage + event.finalDamage * (levelCount * 8 * 0.01)
        event.setDamage(EntityDamageEvent.DamageModifier.MAGIC, finalDamage)
    }
}