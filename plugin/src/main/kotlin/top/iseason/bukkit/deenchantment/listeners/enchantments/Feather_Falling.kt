package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import top.iseason.bukkit.deenchantment.events.DeEntityHurtEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkittemplate.config.annotations.Comment
import top.iseason.bukkittemplate.config.annotations.Key

//摔落骨折
object Feather_Falling : BaseEnchant(DeEnchantments.DE_feather_falling) {
    @Key
    @Comment("", "摔落伤害等级乘数,每级增加百分比伤害")
    var damageRate = 0.12

    @Key
    @Comment("", "缓慢药水时间乘数，单位tick")
    var potionTimeRate = 20

    @Key
    @Comment("", "缓慢药水等级乘数")
    var potionLevel = 1

    //摔落事件
    @EventHandler(ignoreCancelled = true)
    fun onEntityDamageEvent(event: DeEntityHurtEvent) {
        val hurtEvent = event.event
        if (hurtEvent.cause != EntityDamageEvent.DamageCause.FALL) return
        val entity = event.entity
        val level = event.getDeLevel()
        if (level <= 0) return
        if (!checkPermission(entity as? Player)) return
        hurtEvent.damage += (hurtEvent.damage * (level * damageRate))
        entity.addPotionEffect(PotionEffect(PotionEffectType.SLOW, level * potionTimeRate, potionLevel * level))
    }
}