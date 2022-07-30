package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import top.iseason.bukkit.bukkittemplate.config.annotations.Comment
import top.iseason.bukkit.bukkittemplate.config.annotations.Key
import top.iseason.bukkit.deenchantment.events.DeEntityAttackEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkit.deenchantment.utils.EntityTools

//截肢之友
object Bane_Of_Arthropods : BaseEnchant(DeEnchantments.DE_bane_of_arthropods) {
    @Key
    @Comment("", "每级减少等级乘数")
    var reduceRate = 2.5

    @EventHandler(ignoreCancelled = true)
    fun onDeEntityMainHandEvent(event: DeEntityAttackEvent) {
        val entity = event.event.entity as? LivingEntity ?: return
        if (!EntityTools.isArthropods(entity)) return
        val level = event.getDeLevel()
        val damage = event.event.getDamage(EntityDamageEvent.DamageModifier.BASE) - reduceRate * level
        event.event.setDamage(EntityDamageEvent.DamageModifier.BASE, damage)
    }
}