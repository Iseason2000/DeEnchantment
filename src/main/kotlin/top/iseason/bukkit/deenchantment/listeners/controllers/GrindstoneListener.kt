package top.iseason.bukkit.deenchantment.listeners.controllers

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import top.iseason.bukkit.bukkittemplate.utils.submit
import top.iseason.bukkit.deenchantment.utils.EnchantTools


class GrindstoneListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEnchantGrindStone(event: InventoryClickEvent) {
        val player = event.whoClicked as Player
        val top = player.openInventory
        if (top.type != InventoryType.GRINDSTONE) return
        submit {
            val result = top.getItem(2)
            if (result == null || !result.hasItemMeta()) return@submit
            val itemMeta = result.itemMeta ?: return@submit
            EnchantTools.clearEnchantLore(itemMeta)
            result.itemMeta = itemMeta
        }
    }
}

