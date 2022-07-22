package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityShootBowEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.ConfigManager
import top.iseason.bukkit.deenchantment.manager.DeEnchantment
import top.iseason.bukkit.deenchantment.utils.runnables.MultiShot

//多重
class Infinity : BaseEnchant(DeEnchantment.DE_infinity) {
    @EventHandler
    fun onEntityShootBowEvent(event: EntityShootBowEvent) {
        if (event.isCancelled) return
        val bow = event.bow ?: return
        val level = bow.enchantments[DeEnchantment.DE_infinity] ?: return
        if (level <= 0) return
        val projectile = event.projectile
        if (projectile !is Projectile) return
        val entity = event.entity
        MultiShot(entity, level, projectile).runTask(ConfigManager.getPlugin())

    }
}