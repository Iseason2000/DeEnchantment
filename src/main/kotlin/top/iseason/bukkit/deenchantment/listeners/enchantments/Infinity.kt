package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import top.iseason.bukkit.bukkittemplate.config.annotations.Comment
import top.iseason.bukkit.bukkittemplate.config.annotations.Key
import top.iseason.bukkit.deenchantment.events.DeEntityShootBowEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkit.deenchantment.runnables.MultiShot

//多重
object Infinity : BaseEnchant(DeEnchantments.DE_infinity) {
    @Key
    @Comment("", "多重的类型，0为横向，1为随机散射")
    var type = 0

    @Key
    @Comment("", "偏离角度")
    var angle = 0.2

    @EventHandler(ignoreCancelled = true)
    fun onEntityShootBowEvent(event: DeEntityShootBowEvent) {
        val projectile = event.projectile
        if (projectile !is Projectile) return
        val level = event.getDeLevel()
        if (level <= 0) return
        if (!checkPermission(event.entity as? Player)) return
        val entity = event.entity
        val multiShot = MultiShot(entity, level, projectile, angle)
        if (type == 1) multiShot.randomType()
        else multiShot.sweepType()
    }
}