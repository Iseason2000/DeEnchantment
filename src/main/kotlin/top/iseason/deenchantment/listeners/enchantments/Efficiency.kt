package top.iseason.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import top.iseason.deenchantment.manager.DeEnchantment
import top.iseason.deenchantment.utils.Tools

//低效
class Efficiency : Listener {
    @EventHandler
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        if (event.isCancelled) return
        val level = event.player.inventory.itemInMainHand.enchantments[DeEnchantment.DE_efficiency] ?: return
        if (level <= 0) return
        if (Tools.getRandomDouble() >= level * 0.08) return
        event.isCancelled = true
    }

}