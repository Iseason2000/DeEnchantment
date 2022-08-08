package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import top.iseason.bukkit.bukkittemplate.config.annotations.Comment
import top.iseason.bukkit.bukkittemplate.config.annotations.Key
import top.iseason.bukkit.deenchantment.events.DeEntityAttackEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments

//磨钝
object Sharpness : BaseEnchant(DeEnchantments.DE_sharpness) {
    @Key
    @Comment("", "伤害减少等级乘数")
    var damageRate = 0.5

    @EventHandler(ignoreCancelled = true)
    fun onEntityDamageByEntityEvent(event: DeEntityAttackEvent) {
        val entityEvent = event.event
        val entity = entityEvent.entity
        if (entity !is LivingEntity) return
        val level = event.getDeLevel()
        if (level <= 0) return
        if (!checkPermission(entity as? Player)) return
        var damage = entityEvent.getDamage(EntityDamageEvent.DamageModifier.BASE) - damageRate * level
        if (damage < 0) damage = 0.0
        entityEvent.setDamage(EntityDamageEvent.DamageModifier.BASE, damage)
    }
}