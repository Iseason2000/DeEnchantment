package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.Guardian
import org.bukkit.entity.WaterMob
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import top.iseason.bukkit.bukkittemplate.config.annotations.Comment
import top.iseason.bukkit.bukkittemplate.config.annotations.Key
import top.iseason.bukkit.deenchantment.events.DeEntityAttackEvent
import top.iseason.bukkit.deenchantment.events.DeEntityProjectileEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments

//刺穿
object Impaling : BaseEnchant(DeEnchantments.DE_impaling) {
    @Key
    @Comment("", "近战伤害增加等级乘数")
    var meleeDamageRate = 0.5

    @Key
    @Comment("", "弹射物伤害增加等级乘数")
    var remoteDamageRate = 1.0

    @EventHandler
    fun onDeEntityAttackEvent(event: DeEntityAttackEvent) {
        val otherEvent = event.event
        val entity = otherEvent.entity
        if (entity is WaterMob || entity is Guardian) return
        val level = event.getDeLevel()
        if (level <= 0) return
        val damage = otherEvent.getDamage(EntityDamageEvent.DamageModifier.BASE) + meleeDamageRate * level
        otherEvent.setDamage(EntityDamageEvent.DamageModifier.BASE, damage)
    }

    @EventHandler
    fun onDeEntityAttackEvent(event: DeEntityProjectileEvent) {
        val otherEvent = event.event
        val entity = otherEvent.entity
        if (entity is WaterMob || entity is Guardian) return
        val level = event.getDeLevel()
        if (level <= 0) return
        val damage = otherEvent.getDamage(EntityDamageEvent.DamageModifier.BASE) + meleeDamageRate * level
        otherEvent.setDamage(EntityDamageEvent.DamageModifier.BASE, damage)
    }
}