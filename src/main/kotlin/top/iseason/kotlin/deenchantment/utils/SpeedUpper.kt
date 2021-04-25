package top.iseason.kotlin.deenchantment.utils

import org.bukkit.entity.LivingEntity
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable

class SpeedUpper(val entity: LivingEntity, val level: Int) : BukkitRunnable() {
    override fun run() {
        entity.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 220, level - 1))
    }
}