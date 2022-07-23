package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments

//彻底粉碎
object Silk_Touch : BaseEnchant(DeEnchantments.DE_silk_touch) {
    @EventHandler
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        if (event.isCancelled) return
        val level = event.player.inventory.itemInMainHand.enchantments[DeEnchantments.DE_silk_touch] ?: return
        if (level <= 0) return
        event.isDropItems = false
    }
}