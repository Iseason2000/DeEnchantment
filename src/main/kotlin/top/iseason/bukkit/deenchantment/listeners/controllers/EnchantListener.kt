package top.iseason.bukkit.deenchantment.listeners.controllers

import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.inventory.EnchantingInventory
import top.iseason.bukkit.bukkittemplate.debug.SimpleLogger
import top.iseason.bukkit.bukkittemplate.utils.sendColorMessage
import top.iseason.bukkit.deenchantment.DeEnchantment
import top.iseason.bukkit.deenchantment.utils.EnchantTools

class EnchantListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onEnchantItemEvent(event: EnchantItemEvent) {
        if (event.isCancelled) return
        val enchantsToAdd = event.enchantsToAdd
        val translateEnchantByChance = EnchantTools.translateEnchantByChance(enchantsToAdd)
        val inventory = event.inventory as EnchantingInventory
        EnchantTools.LoreSetter(inventory, translateEnchantByChance)
            .runTaskLater(DeEnchantment.javaPlugin, 1)
        if (enchantsToAdd != translateEnchantByChance) {
            event.enchanter.sendColorMessage("${SimpleLogger.prefix}${ChatColor.YELLOW}你的附魔发生了某些变化！")
        }
    }
}