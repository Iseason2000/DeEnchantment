package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.GameMode
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Trident
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import top.iseason.bukkit.bukkittemplate.config.annotations.Comment
import top.iseason.bukkit.bukkittemplate.config.annotations.Key
import top.iseason.bukkit.bukkittemplate.utils.RandomUtils
import top.iseason.bukkit.bukkittemplate.utils.bukkit.checkAir
import top.iseason.bukkit.bukkittemplate.utils.bukkit.giveItems
import top.iseason.bukkit.bukkittemplate.utils.sendColorMessage
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments

//背叛
object Loyalty : BaseEnchant(DeEnchantments.DE_loyalty) {
    @Key
    @Comment("", "触发概率等级乘数")
    var chanceRate: Double = 0.1

    @Key
    @Comment("", "宿主搜索半径")
    var radius: Double = 10.0

    @Key
    @Comment("", "是否高亮宿主")
    var highlight: Boolean = true

    @Key
    @Comment("", "提示消息")
    var message = "&c您的武器已背叛！&6现在属于 &a{player} &6位于 &a{location}"

    @EventHandler
    fun onProjectileHitEvent(event: ProjectileHitEvent) {
        val entity = event.entity
        if (entity !is Trident) return
        val level = entity.item.getDeLevel()
        if (level <= 0) return
        if (!checkPermission(entity.shooter as? Player)) return
        //判断背叛
        if (RandomUtils.getDouble() > level * chanceRate) return
        for (nearbyEntity in entity.getNearbyEntities(radius, radius, radius)) {
            if (nearbyEntity !is LivingEntity || nearbyEntity == entity.shooter) continue
            val trident = entity.item
            val equipment = nearbyEntity.equipment ?: continue
            val itemInMainHand = equipment.itemInMainHand
            equipment.setItemInMainHand(trident)
            if (entity.shooter is LivingEntity && entity.shooter !is Player) {
                (entity.shooter as LivingEntity).equipment?.setItemInMainHand(null)
            }
            //替换玩家手上物品
            if (!itemInMainHand.type.checkAir() && nearbyEntity is Player && nearbyEntity.gameMode != GameMode.CREATIVE) {
                nearbyEntity.giveItems(itemInMainHand)
            }
            //设置生物掉落几率
            if (entity !is Player) {
                try {
                    equipment.itemInMainHandDropChance = 1.0F
                } catch (_: Exception) {
                }
            }
            val shooter = entity.shooter
            if (highlight)
                nearbyEntity.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, 200, 0))
            if (shooter is Player)
                shooter.sendColorMessage(
                    message.replace("{player}", nearbyEntity.name)
                        .replace(
                            "{location}",
                            "${nearbyEntity.location.blockX},${nearbyEntity.location.blockY},${nearbyEntity.location.blockZ}"
                        )
                )
            entity.remove()
            break
        }
    }
}