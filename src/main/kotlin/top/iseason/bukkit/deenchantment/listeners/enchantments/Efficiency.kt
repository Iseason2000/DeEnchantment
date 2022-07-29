package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import top.iseason.bukkit.bukkittemplate.config.annotations.Comment
import top.iseason.bukkit.bukkittemplate.config.annotations.Key
import top.iseason.bukkit.bukkittemplate.utils.RandomUtils
import top.iseason.bukkit.deenchantment.events.DeBreakBlockEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments

//低效
object Efficiency : BaseEnchant(DeEnchantments.DE_efficiency) {

    @Key
    @Comment("", "触发概率乘数, 0~1")
    var rate = 0.08

    @EventHandler
    fun onBlockBreakEvent(event: DeBreakBlockEvent) {
        val level = event.getDeLevel()
        if (level <= 0) return
        if (RandomUtils.getDouble() >= level * rate) return
        event.isCancelled = true
    }

}