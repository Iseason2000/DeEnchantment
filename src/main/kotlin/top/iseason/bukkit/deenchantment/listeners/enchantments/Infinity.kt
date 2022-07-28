package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import top.iseason.bukkit.bukkittemplate.utils.submit
import top.iseason.bukkit.deenchantment.events.DeEntityShootBowEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkit.deenchantment.runnables.MultiShot

//多重 TODO: 多种方式
object Infinity : BaseEnchant(DeEnchantments.DE_infinity) {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    fun onEntityShootBowEvent(event: DeEntityShootBowEvent) {
        val projectile = event.projectile
        if (projectile !is Projectile) return
        val level = event.getDeLevel()
        if (level <= 0) return
        val entity = event.entity
        submit(task = MultiShot(entity, level, projectile))
    }
}