package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkit.deenchantment.utils.EnchantTools
import top.iseason.bukkit.deenchantment.utils.Tools

object Blast_Protection : BaseEnchant(DeEnchantments.DE_blast_protection) {
    //瞬间爆炸 受到攻击概率爆炸
    @EventHandler
    fun onEntityDamageEvent(event: EntityDamageEvent) {
        if (event.isCancelled) return
        if (event.cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION ||
            event.cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
        ) return
        val entity = event.entity
        if (entity !is LivingEntity) return
        val levelCount = EnchantTools.getLevelCount(entity, DeEnchantments.DE_blast_protection)
        if (levelCount == 0) return
        if (Tools.getRandomDouble() < levelCount * 0.05)
            entity.getWorld().createExplosion(entity.getLocation(), (levelCount * 0.2).toFloat(), false, false, entity)
    }

}