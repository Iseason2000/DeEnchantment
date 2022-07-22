package top.iseason.bukkit.deenchantment.listeners.controllers

import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.inventory.EnchantingInventory
import top.iseason.bukkit.deenchantment.manager.ConfigManager
import top.iseason.bukkit.deenchantment.utils.EnchantTools
import top.iseason.bukkit.deenchantment.utils.LogSender

class EnchantListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onEnchantItemEvent(event: EnchantItemEvent) {
        if (event.isCancelled) return
        val enchantsToAdd = event.enchantsToAdd
        val translateEnchantByChance = EnchantTools.translateEnchantByChance(enchantsToAdd)
        val inventory = event.inventory as EnchantingInventory
        EnchantTools.LoreSetter(inventory, translateEnchantByChance)
            .runTaskLater(ConfigManager.getPlugin(), 1)
        if (enchantsToAdd != translateEnchantByChance) {
            LogSender.log(event.enchanter, "${ChatColor.YELLOW}你的附魔发生了某些变化！")
        }
    }
}