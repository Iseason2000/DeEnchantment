package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.entity.Trident
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments

//逆流
object Riptide : BaseEnchant(DeEnchantments.DE_riptide) {
    @EventHandler
    fun onProjectileLaunchEvent(event: ProjectileLaunchEvent) {
        if (event.isCancelled) return
        val entity = event.entity
        val shooter = entity.shooter
        if (shooter !is Player) return
        if (entity !is Trident) return
        val level = entity.item.enchantments[DeEnchantments.DE_riptide] ?: return
        if (level <= 0) return
        event.isCancelled = true
        if (!shooter.isInWater && !shooter.world.hasStorm()) return//不考虑下雨，判断太麻烦
        val itemInMainHand = shooter.inventory.itemInMainHand
        val itemMeta = itemInMainHand.itemMeta as Damageable
        itemMeta.damage += 1
        itemInMainHand.itemMeta = itemMeta as ItemMeta
        if (itemMeta.damage >= 250) {
            shooter.inventory.setItemInMainHand(null)
            shooter.playSound(shooter.location, Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F)
        }
        val direction = shooter.location.direction.multiply(-level)
        shooter.velocity = direction
    }

}