package top.iseason.deenchantment.listeners.enchantments

import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityShootBowEvent
import top.iseason.deenchantment.manager.ConfigManager
import top.iseason.deenchantment.manager.DeEnchantment
import top.iseason.deenchantment.utils.runnables.ProjectileShooter

//连珠
class Multishot : Listener {
    @EventHandler
    fun onEntityShootBowEvent(event: EntityShootBowEvent) {
        if (event.isCancelled) return
        val bow = event.bow ?: return
        val level = bow.enchantments[DeEnchantment.DE_multishot] ?: return
        if (level <= 0) return
        val projectile = event.projectile
        if (projectile !is Projectile) return
        val entity = event.entity
        val consumable = event.consumable
        var isConsumable = false
        if (entity is Player && entity.gameMode != GameMode.CREATIVE && consumable != null)
            isConsumable = true
        ProjectileShooter(entity, projectile, level, isConsumable, consumable?.type)
            .runTaskTimer(ConfigManager.getPlugin(), 3, 3)
    }


}