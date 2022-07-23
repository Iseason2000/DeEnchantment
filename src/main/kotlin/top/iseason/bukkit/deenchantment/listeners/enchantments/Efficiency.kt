package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkit.deenchantment.utils.Tools

//低效
object Efficiency : BaseEnchant(DeEnchantments.DE_efficiency) {
    @EventHandler
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        if (event.isCancelled) return
        val level = event.player.inventory.itemInMainHand.enchantments[DeEnchantments.DE_efficiency] ?: return
        if (level <= 0) return
        if (Tools.getRandomDouble() >= level * 0.08) return
        event.isCancelled = true
    }

}