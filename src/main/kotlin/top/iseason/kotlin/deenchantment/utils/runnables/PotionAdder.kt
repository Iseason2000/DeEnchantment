package top.iseason.kotlin.deenchantment.utils.runnables

import org.bukkit.entity.LivingEntity
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable

class PotionAdder(val entity: LivingEntity, val type: PotionEffectType, val time: Int, val level: Int) :
    BukkitRunnable() {
    override fun run() {
        entity.addPotionEffect(PotionEffect(type, time, level - 1))
    }
}