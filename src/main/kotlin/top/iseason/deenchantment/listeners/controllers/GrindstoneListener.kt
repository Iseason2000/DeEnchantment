package top.iseason.deenchantment.listeners.controllers

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import top.iseason.deenchantment.manager.ConfigManager
import top.iseason.deenchantment.utils.EnchantTools


class GrindstoneListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEnchantGrindStone(event: InventoryClickEvent) {
        val player = event.whoClicked as Player
        val top = player.openInventory
        if (top.type != InventoryType.GRINDSTONE) return
        val plugin = ConfigManager.getPlugin()
        plugin.server.scheduler.runTask(plugin, Runnable {
            val result = top.getItem(2)
            if (result == null || !result.hasItemMeta()) return@Runnable
            val itemMeta = result.itemMeta ?: return@Runnable
            EnchantTools.clearEnchantLore(itemMeta)
            result.itemMeta = itemMeta
        })
    }
}

