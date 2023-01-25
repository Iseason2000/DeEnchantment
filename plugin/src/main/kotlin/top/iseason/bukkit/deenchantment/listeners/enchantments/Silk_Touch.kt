package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.inventory.BlockInventoryHolder
import top.iseason.bukkit.deenchantment.events.DeBreakBlockEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkittemplate.config.annotations.Comment
import top.iseason.bukkittemplate.config.annotations.Key

//彻底粉碎
object Silk_Touch : BaseEnchant(DeEnchantments.DE_silk_touch) {
    @Key
    @Comment("", "是否对容器有效,设置为true将会删除容器内的物品")
    var allowContainer = false

    @EventHandler(ignoreCancelled = true)
    fun onBlockBreakEvent(event: DeBreakBlockEvent) {
        val level = event.getDeLevel()
        if (level <= 0) return
        if (!checkPermission(event.player as? Player)) return
        if (!allowContainer && event.event.block.state is BlockInventoryHolder) return
        event.event.isDropItems = false
    }
}