package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantment

//彻底粉碎
class Silk_Touch : BaseEnchant(DeEnchantment.DE_silk_touch) {
    @EventHandler
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        if (event.isCancelled) return
        val level = event.player.inventory.itemInMainHand.enchantments[DeEnchantment.DE_silk_touch] ?: return
        if (level <= 0) return
        event.isDropItems = false
    }
}