package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import top.iseason.bukkit.bukkittemplate.config.annotations.Comment
import top.iseason.bukkit.bukkittemplate.config.annotations.Key
import top.iseason.bukkit.deenchantment.events.DeEntityAttackEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkit.deenchantment.utils.Tools

//引火烧身
object Fire_Aspect : BaseEnchant(DeEnchantments.DE_fire_aspect) {
    @Key
    @Comment("", "触发概率等级乘数，0~1")
    var chanceRate = 0.2

    @Key
    @Comment("", "燃烧时间等级乘数，单位tick")
    var fireTickRate = 20

    @EventHandler(ignoreCancelled = true)
    fun onEntityDamageByEntityEvent(event: DeEntityAttackEvent) {
        val level = event.getDeLevel()
        if (level <= 0) return
        if (Tools.getRandomDouble() >= level * chanceRate) return
        event.entity.fireTicks += (level * fireTickRate)
    }
}