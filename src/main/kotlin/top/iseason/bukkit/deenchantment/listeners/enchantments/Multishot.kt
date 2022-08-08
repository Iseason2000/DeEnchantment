package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import top.iseason.bukkit.bukkittemplate.config.annotations.Comment
import top.iseason.bukkit.bukkittemplate.config.annotations.Key
import top.iseason.bukkit.bukkittemplate.utils.submit
import top.iseason.bukkit.deenchantment.events.DeEntityShootBowEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkit.deenchantment.runnables.ProjectileShooter

//连珠
object Multishot : BaseEnchant(DeEnchantments.DE_multishot) {

    @Key
    @Comment("", "发射间隔, 单位tick")
    var period: Long = 3

    @EventHandler(ignoreCancelled = true)
    fun onEntityShootBowEvent(event: DeEntityShootBowEvent) {
        val bowEvent = event.event
        val level = event.getDeLevel()
        if (level <= 0) return
        if (!checkPermission(event.entity as? Player)) return
        val projectile = event.projectile
        if (projectile !is Projectile) return
        val entity = bowEvent.entity
        val consumable = bowEvent.consumable
        var isConsumable = false
        if (entity is Player && entity.gameMode != GameMode.CREATIVE && consumable != null)
            isConsumable = true
        submit(period, period, task = ProjectileShooter(entity, projectile, level, isConsumable, consumable?.type))

    }


}