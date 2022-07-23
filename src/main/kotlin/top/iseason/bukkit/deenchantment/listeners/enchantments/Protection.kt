package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkit.deenchantment.utils.EnchantTools

//保护不了 附魔 实现
object Protection : BaseEnchant(DeEnchantments.DE_protection) {
    @EventHandler
    fun onEntityDamageEvent(event: EntityDamageEvent) {
        if (event.isCancelled) return
        val entity = event.entity
        if (entity !is LivingEntity) return
        val levelCount = EnchantTools.getLevelCount(entity, DeEnchantments.DE_protection)
        event.damage = event.damage + event.damage * (levelCount * 4 * 0.01)
    }
}