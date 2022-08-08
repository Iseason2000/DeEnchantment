package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import top.iseason.bukkit.bukkittemplate.config.annotations.Comment
import top.iseason.bukkit.bukkittemplate.config.annotations.Key
import top.iseason.bukkit.deenchantment.events.DeEntityHurtEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments

//保护不了 附魔 实现
object Protection : BaseEnchant(DeEnchantments.DE_protection) {
    @Key
    @Comment("", "伤害增加等级系数, 0~1")
    var damageRate = 0.04

    @EventHandler(ignoreCancelled = true)
    fun onEntityDamageEvent(event: DeEntityHurtEvent) {
        val damageEvent = event.event
        val level = event.getDeLevel()
        if (level == 0) return
        if (!checkPermission(event.entity as? Player)) return
        val d = damageEvent.getDamage(EntityDamageEvent.DamageModifier.BASE)
        val damage = d + d * (level * damageRate)
        damageEvent.setDamage(EntityDamageEvent.DamageModifier.BASE, damage)
    }
}