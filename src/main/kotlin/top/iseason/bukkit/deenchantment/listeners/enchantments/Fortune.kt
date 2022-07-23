package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockDropItemEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkit.deenchantment.utils.Tools

//时运不济
object Fortune : BaseEnchant(DeEnchantments.DE_fortune) {
    @EventHandler
    fun onBlockDropItemEvent(event: BlockDropItemEvent) {
        if (event.isCancelled) return
        val items = event.items
        if (items.size != 1) return
        val player = event.player
        val item = player.equipment?.itemInMainHand ?: return
        val level = item.enchantments[DeEnchantments.DE_fortune] ?: return
        if (level <= 0) return
        if (Tools.getRandomDouble() > level * 0.03) return
        event.items.removeAt(0)
    }
}