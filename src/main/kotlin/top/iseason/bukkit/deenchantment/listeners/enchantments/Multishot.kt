package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityShootBowEvent
import top.iseason.bukkit.bukkittemplate.utils.submit
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkit.deenchantment.utils.runnables.ProjectileShooter

//连珠
object Multishot : BaseEnchant(DeEnchantments.DE_multishot) {
    @EventHandler
    fun onEntityShootBowEvent(event: EntityShootBowEvent) {
        if (event.isCancelled) return
        val bow = event.bow ?: return
        val level = bow.enchantments[DeEnchantments.DE_multishot] ?: return
        if (level <= 0) return
        val projectile = event.projectile
        if (projectile !is Projectile) return
        val entity = event.entity
        val consumable = event.consumable
        var isConsumable = false
        if (entity is Player && entity.gameMode != GameMode.CREATIVE && consumable != null)
            isConsumable = true
        submit(3, 3, task = ProjectileShooter(entity, projectile, level, isConsumable, consumable?.type))

    }


}