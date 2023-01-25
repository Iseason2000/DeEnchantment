package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import top.iseason.bukkit.deenchantment.events.DeEntityAttackEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkittemplate.config.annotations.Comment
import top.iseason.bukkittemplate.config.annotations.Key

//牵引之刃
object Sweeping : BaseEnchant(DeEnchantments.DE_sweeping) {
    @Key
    @Comment("", "吸引力量等级乘数")
    var powerRate = 0.2

    @EventHandler(ignoreCancelled = true)
    fun onEntityDamageByEntityEvent(event: DeEntityAttackEvent) {
        val entityEvent = event.event
        if (entityEvent.cause != EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) return
        val level = event.getDeLevel()
        if (level <= 0) return
        if (!checkPermission(event.entity as? Player)) return
        val v1 = entityEvent.entity.location.toVector()
        val v2 = event.entity.location.toVector()
        entityEvent.entity.velocity = v2.subtract(v1).normalize().multiply(level * powerRate)
    }
}