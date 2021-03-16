package top.iseason.kotlin.deenchantment.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.inventory.EnchantingInventory
import top.iseason.kotlin.deenchantment.manager.ConfigManager
import top.iseason.kotlin.deenchantment.utils.EnchantTools

class EnchantListener : Listener {
    @EventHandler(priority = EventPriority.LOW)
    fun onEnchantItemEvent(event: EnchantItemEvent) {
        if (event.isCancelled) return
        val enchantsToAdd = event.enchantsToAdd
        val translateEnchantByChance = EnchantTools.translateEnchantByChance(enchantsToAdd)
        val inventory = event.inventory as EnchantingInventory
        EnchantTools.LoreSetter(inventory, translateEnchantByChance)
            .runTaskLater(ConfigManager.getPlugin(),2)
    }
}