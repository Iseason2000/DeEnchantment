package top.iseason.kotlin.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent
import top.iseason.kotlin.deenchantment.manager.DeEnchantment
import top.iseason.kotlin.deenchantment.utils.Tools

//时运不济
class Fortune : Listener {
    //todo:未完成
    @EventHandler
    fun onBlockBreakEvent(event: BlockDropItemEvent) {
        if (event.isCancelled) return
        val items = event.items
        if (items.size != 1) return
        val player = event.player
        val item = player.equipment?.itemInMainHand ?: return
        val level = item.enchantments[DeEnchantment.DE_fortune] ?: return
        if (level <= 0) return
        if (Tools.getRandomDouble() > level * 0.03) return
        event.items.removeAt(0)
    }
}