package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import top.iseason.bukkit.deenchantment.events.DeEntityAttackEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkittemplate.config.annotations.Comment
import top.iseason.bukkittemplate.config.annotations.Key

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
        if (!checkPermission(event.entity as? Player)) return
        val direction = entity.location.direction.normalize().multiply(-1)
        entity.velocity = entity.velocity.add(direction.multiply(level * knockBackRate))
    }
}