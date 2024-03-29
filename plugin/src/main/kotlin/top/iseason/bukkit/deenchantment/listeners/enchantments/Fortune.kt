package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.inventory.BlockInventoryHolder
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkittemplate.config.annotations.Comment
import top.iseason.bukkittemplate.config.annotations.Key
import top.iseason.bukkittemplate.utils.other.RandomUtils

//时运不济
object Fortune : BaseEnchant(DeEnchantments.DE_fortune) {
    @Key
    @Comment("", "触发概率等级乘数, 0~1")
    var chanceRate = 0.05

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onBlockDropItemEvent(event: BlockDropItemEvent) {
        val items = event.items
        if (items.isEmpty()) return
        val state = event.blockState
        //是否是容器
        if (state is BlockInventoryHolder) return
        // 掉落自身,没有额外掉落物
        if (items.size == 1 && items[0].itemStack.amount == 1 && items[0].itemStack.type == state.type) return
        val player = event.player
        val item = player.equipment?.itemInMainHand ?: return
        val level = item.getDeLevel()
        if (level <= 0) return
        if (!checkPermission(player)) return
        for (i in items.size - 1 downTo 0) {
            if (RandomUtils.getDouble() > level * chanceRate) continue
            items.removeAt(i)
        }
    }
}