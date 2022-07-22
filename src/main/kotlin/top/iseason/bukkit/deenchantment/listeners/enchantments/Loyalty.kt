package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Trident
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantment
import top.iseason.bukkit.deenchantment.utils.LogSender
import top.iseason.bukkit.deenchantment.utils.Tools

//背叛
class Loyalty : BaseEnchant(DeEnchantment.DE_loyalty) {
    @EventHandler
    fun onProjectileLaunchEvent(event: ProjectileHitEvent) {
        val entity = event.entity
        if (entity !is Trident) return
        val level = entity.item.enchantments[DeEnchantment.DE_loyalty] ?: return
        if (level <= 0) return
        if (Tools.getRandomDouble() > level * 0.1) return
        val distance = level * 5.0
        for (nearbyEntity in entity.getNearbyEntities(distance, distance, distance)) {
            if (nearbyEntity !is LivingEntity || nearbyEntity == entity.shooter) continue
            val trident = entity.item
            val equipment = nearbyEntity.equipment ?: continue
            val itemInMainHand = equipment.itemInMainHand
            equipment.setItemInMainHand(trident)
            //替换玩家手上物品
            if (itemInMainHand.hasItemMeta() && nearbyEntity is Player && nearbyEntity.gameMode != GameMode.CREATIVE) {
                for (item in nearbyEntity.inventory.addItem(itemInMainHand).values) {
                    if (item.hasItemMeta()) {
                        nearbyEntity.location.world?.dropItem(nearbyEntity.location, item)
                        break
                    }
                }
            }
            //设置生物掉落几率
            if (entity !is Player) {
                try {
                    equipment.itemInMainHandDropChance = 1.0F
                } catch (e: Exception) {
                }
            }

            val shooter = entity.shooter
            nearbyEntity.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, 200, 0))
            if (shooter is Player)
                LogSender.log(
                    shooter,
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