package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.CrossbowMeta
import top.iseason.bukkit.bukkittemplate.config.annotations.Comment
import top.iseason.bukkit.bukkittemplate.config.annotations.Key
import top.iseason.bukkit.bukkittemplate.utils.submit
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments

object Quick_Charge : BaseEnchant(DeEnchantments.DE_quick_charge) {
    @Key
    @Comment("", "打断次数等级系数，一次2tick")
    var timeRate = 1

    private val chargeMap = HashMap<ItemStack, Int>()

    @EventHandler
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        val item = event.item ?: return
        val itemMeta = item.itemMeta ?: return
        if (itemMeta !is CrossbowMeta) return
        if (itemMeta.hasChargedProjectiles()) return//有箭取消
        val level = item.getDeLevel()
        if (level <= 0) return
        val count = chargeMap[item] ?: (level * timeRate) //检查次数，没有赋值
        if (count <= 0) { //次数达到
            chargeMap.remove(item)
            return
        }
        chargeMap[item] = count - 1 //次数减一
        val hand = event.hand ?: return
        //取消拉弓
        val player = event.player
        player.equipment?.setItem(hand, null)
        submit(delay = 2) {
            player.equipment?.setItem(hand, item)
        }
    }
}
