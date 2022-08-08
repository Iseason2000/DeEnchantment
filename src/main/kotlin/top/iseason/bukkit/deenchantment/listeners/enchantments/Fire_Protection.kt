package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import top.iseason.bukkit.bukkittemplate.config.annotations.Comment
import top.iseason.bukkit.bukkittemplate.config.annotations.Key
import top.iseason.bukkit.bukkittemplate.utils.RandomUtils
import top.iseason.bukkit.deenchantment.events.DeEntityHurtEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments

//火焰烧灼
object Fire_Protection : BaseEnchant(DeEnchantments.DE_fire_protection) {
    @Key
    @Comment("", "触发概率等级乘数, 0~1")
    var chanceRate = 0.05

    @Key
    @Comment("", "着火时间等级乘数,单位tick")
    var fireTimeRate = 20

    //受到攻击有概率着火
    @EventHandler(ignoreCancelled = true)
    fun onEntityDamageEvent(event: DeEntityHurtEvent) {
        val hurtEvent = event.event
        if (hurtEvent.cause == DamageCause.FIRE && hurtEvent.cause == DamageCause.FIRE_TICK) return
        val entity = event.entity
        val level = event.getDeLevel()
        if (!checkPermission(entity as? Player)) return
        if (level <= 0) return
        if (RandomUtils.getDouble() < level * chanceRate)
            entity.fireTicks = level * fireTimeRate
    }
//    @EventHandler //着火增加伤害
//    fun onEntityCombustByBlockEvent(event: EntityCombustEvent) {
//        if (event.isCancelled) return
//        val entity = event.entity
//        if (entity !is LivingEntity) return
//        val equipments = entity.equipment?.armorContents ?: return
//        var maxLevel = 0
//        for (equipment in equipments) {
//            val level = equipment?.enchantments?.get(DeEnchantments.DE_fire_protection) ?: continue
//            if (level > maxLevel) maxLevel = level
//        }
//        if (maxLevel == 0) return
//        val duration = event.duration * ((15 * maxLevel) * 0.01 + 1)
//        event.duration = duration.toInt()
//    }

}