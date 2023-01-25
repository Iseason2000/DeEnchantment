package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDeathEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkittemplate.config.annotations.Comment
import top.iseason.bukkittemplate.config.annotations.Key
import top.iseason.bukkittemplate.utils.other.RandomUtils

//知足
object Looting : BaseEnchant(DeEnchantments.DE_looting) {
    @Key
    @Comment("", "触发概率等级乘数, 0~1")
    var chanceRate = 0.05

    @EventHandler
    fun onEntityDeathEvent(event: EntityDeathEvent) {
        val killer = event.entity.killer ?: return
        val drops = event.drops
        if (drops.isEmpty()) return
        val level = killer.inventory.itemInMainHand.getDeLevel()
        if (level <= 0) return
        if (!checkPermission(killer as? Player)) return
        if (RandomUtils.getDouble() >= level * chanceRate) return
        drops.clear()
    }
}