package top.iseason.kotlin.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.CrossbowMeta
import top.iseason.kotlin.deenchantment.manager.ConfigManager
import top.iseason.kotlin.deenchantment.manager.DeEnchantment
import top.iseason.kotlin.deenchantment.utils.Runable

class Quick_Charge : Listener {
    val chargeMap = HashMap<ItemStack, Int>()

    @EventHandler
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        val item = event.item ?: return
        val level = item.enchantments[DeEnchantment.DE_quick_charge] ?: return//检查附魔
        if (level <= 0) return
        val clone = item.clone()
        val itemMeta = clone.itemMeta
        if (itemMeta !is CrossbowMeta) return
        if (itemMeta.hasChargedProjectiles()) return//有箭取消
        val count = chargeMap[clone] ?: level * 2 //检查次数，没有赋值
        if (count <= 0) { //次数达到
            chargeMap.remove(clone)
            return
        }
        chargeMap[clone] = count - 1 //次数减一
        val hand = event.hand ?: return
        //取消拉弓
        event.player.equipment?.setItem(hand, null)
        Runable(event.player, hand, clone).runTaskLater(ConfigManager.getPlugin(), 2)
    }
}
