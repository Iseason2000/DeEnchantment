package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantment
import top.iseason.bukkit.deenchantment.utils.EnchantTools
import top.iseason.bukkit.deenchantment.utils.Tools

//火焰烧灼
class Fire_Protection : BaseEnchant(DeEnchantment.DE_fire_protection) {
    //受到攻击有概率着火
    @EventHandler
    fun onEntityDamageEvent(event: EntityDamageEvent) {
        if (event.isCancelled) return
        if (event.cause == DamageCause.FIRE && event.cause == DamageCause.FIRE_TICK) return
        val entity = event.entity
        if (entity !is LivingEntity) return
        val levelCount = EnchantTools.getLevelCount(entity, DeEnchantment.DE_fire_protection)
        if (levelCount == 0) return
        if (Tools.getRandomDouble() < levelCount * 0.05)
            entity.fireTicks = levelCount * 20
    }
//    @EventHandler //着火增加伤害
//    fun onEntityCombustByBlockEvent(event: EntityCombustEvent) {
//        if (event.isCancelled) return
//        val entity = event.entity
//        if (entity !is LivingEntity) return
//        val equipments = entity.equipment?.armorContents ?: return
//        var maxLevel = 0
//        for (equipment in equipments) {
//            val level = equipment?.enchantments?.get(DeEnchantment.DE_fire_protection) ?: continue
//            if (level > maxLevel) maxLevel = level
//        }
//        if (maxLevel == 0) return
//        val duration = event.duration * ((15 * maxLevel) * 0.01 + 1)
//        event.duration = duration.toInt()
//    }


}