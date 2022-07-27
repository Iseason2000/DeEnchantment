package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import top.iseason.bukkit.bukkittemplate.config.annotations.Comment
import top.iseason.bukkit.bukkittemplate.config.annotations.Key
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkit.deenchantment.utils.Tools

//低效 TODO: 更换自定义事件
object Efficiency : BaseEnchant(DeEnchantments.DE_efficiency) {

    @Key
    @Comment("", "触发概率乘数")
    var rate = 0.08

    @EventHandler
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        if (event.isCancelled) return
        val level = event.player.getHandDeEnchant()
        if (level <= 0) return
        if (Tools.getRandomDouble() >= level * rate) return
        event.isCancelled = true
    }

}