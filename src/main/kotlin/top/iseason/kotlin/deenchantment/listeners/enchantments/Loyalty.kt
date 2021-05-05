package top.iseason.kotlin.deenchantment.listeners.enchantments

import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Trident
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import top.iseason.kotlin.deenchantment.manager.DeEnchantment
import top.iseason.kotlin.deenchantment.utils.Tools

//背叛
class Loyalty : Listener {
    @EventHandler
    fun onProjectileLaunchEvent(event: ProjectileHitEvent) {
        val entity = event.entity
        if (entity !is Trident) return
        val level = entity.item.enchantments[DeEnchantment.DE_loyalty] ?: return
        if (level <= 0) return
        if (Tools.getRandomDouble() > level * 0.1) return
        val distance = level * 5.0
        val nearbyEntities = entity.getNearbyEntities(distance, distance, distance)
        if (nearbyEntities.isEmpty()) return
        for (nearbyEntity in nearbyEntities) {
            if (nearbyEntity !is LivingEntity || nearbyEntity == entity.shooter) continue
            val trident = entity.item
            val equipment = nearbyEntity.equipment ?: continue
            val itemInMainHand = equipment.itemInMainHand
            equipment.setItemInMainHand(trident)
            if (itemInMainHand.hasItemMeta())
                if (nearbyEntity is Player && nearbyEntity.gameMode != GameMode.CREATIVE) {
                    val addItem = nearbyEntity.inventory.addItem(itemInMainHand)
                    if (addItem.isNotEmpty()) {
                        for (item in addItem.values) {
                            nearbyEntity.location.world?.dropItem(nearbyEntity.location, item)
                        }
                    }
                } else {
                    nearbyEntity.location.world?.dropItem(nearbyEntity.location, itemInMainHand)
                }
            if (entity !is Player) {
                try {
                    equipment.itemInMainHandDropChance = 1.0F
                } catch (e: Exception) {
                }
            }
            val shooter = entity.shooter
            nearbyEntity.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, 200, 0))
            if (shooter is Player)
                shooter.sendMessage(
                    "${ChatColor.RED}您的武器已背叛！${ChatColor.YELLOW}现在属于" +
                            "${ChatColor.AQUA}${nearbyEntity.name} ${ChatColor.YELLOW}" +
                            "位于${ChatColor.GREEN}" +
                            "${nearbyEntity.location.blockX},${nearbyEntity.location.blockY},${nearbyEntity.location.blockZ}"
                )
            entity.remove()
            break
        }

    }
}