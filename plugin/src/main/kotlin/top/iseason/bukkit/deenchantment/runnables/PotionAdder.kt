package top.iseason.bukkit.deenchantment.runnables

import org.bukkit.entity.LivingEntity
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable

class PotionAdder(val entity: LivingEntity, val type: PotionEffectType, private val time: Int, var level: Int) :
    BukkitRunnable() {
    override fun run() {
        entity.addPotionEffect(PotionEffect(type, time, level - 1))
    }

    override fun cancel() {
        if (isCancelled) return
        entity.removePotionEffect(type)
        super.cancel()
    }

}