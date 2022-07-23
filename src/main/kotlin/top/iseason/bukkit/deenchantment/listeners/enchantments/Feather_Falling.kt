package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments

//摔落骨折
object Feather_Falling : BaseEnchant(DeEnchantments.DE_feather_falling) {
    //摔落事件
    @EventHandler
    fun onEntityDamageEvent(event: EntityDamageEvent) {
        if (event.isCancelled) return
        if (event.cause != EntityDamageEvent.DamageCause.FALL) return
        val entity = event.entity
        if (entity !is LivingEntity) return
        val enchantments = entity.equipment?.boots?.enchantments ?: return
        val level = enchantments[DeEnchantments.DE_feather_falling] ?: return
        if (level <= 0) return
        event.damage = event.damage + event.damage * (level * 12 * 0.01)
        entity.addPotionEffect(PotionEffect(PotionEffectType.SLOW, level * 20, 3))
    }
}