package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import top.iseason.bukkit.bukkittemplate.config.annotations.Comment
import top.iseason.bukkit.bukkittemplate.config.annotations.Key
import top.iseason.bukkit.deenchantment.events.DeEntityAttackEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments

//退击
object KnockBack : BaseEnchant(DeEnchantments.DE_knockback) {
    @Key
    @Comment("", "退击矢量等级乘数")
    var knockBackRate = 0.6

    @EventHandler(ignoreCancelled = true)
    fun onEntityDamageByEntityEvent(event: DeEntityAttackEvent) {
        val entity = event.entity
        val level = event.getDeLevel()
        if (level <= 0) return
        val direction = entity.location.direction.normalize().multiply(-1)
        entity.velocity = entity.velocity.add(direction.multiply(level * knockBackRate))
    }
}