package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityShootBowEvent
import top.iseason.bukkit.bukkittemplate.utils.submit
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkit.deenchantment.utils.runnables.MultiShot

//多重
object Infinity : BaseEnchant(DeEnchantments.DE_infinity) {
    @EventHandler
    fun onEntityShootBowEvent(event: EntityShootBowEvent) {
        if (event.isCancelled) return
        val bow = event.bow ?: return
        val level = bow.enchantments[DeEnchantments.DE_infinity] ?: return
        if (level <= 0) return
        val projectile = event.projectile
        if (projectile !is Projectile) return
        val entity = event.entity
        submit(task = MultiShot(entity, level, projectile))
    }
}