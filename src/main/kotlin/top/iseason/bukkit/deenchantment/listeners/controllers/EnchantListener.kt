package top.iseason.bukkit.deenchantment.listeners.controllers

import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.inventory.EnchantingInventory
import top.iseason.bukkit.bukkittemplate.utils.bukkit.applyMeta
import top.iseason.bukkit.bukkittemplate.utils.sendColorMessage
import top.iseason.bukkit.bukkittemplate.utils.submit
import top.iseason.bukkit.deenchantment.settings.Message
import top.iseason.bukkit.deenchantment.utils.EnchantTools

object EnchantListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEnchantItemEvent(event: EnchantItemEvent) {
        val enchantsToAdd = event.enchantsToAdd
        val inventory = event.inventory as EnchantingInventory
        submit {
            val translateEnchantByChance = EnchantTools.translateEnchantByChance(enchantsToAdd)
            val itemStack = inventory.item?.clone() ?: return@submit
            EnchantTools.clearEnchants(itemStack)
            itemStack.applyMeta {
                EnchantTools.addEnchants(this, translateEnchantByChance as MutableMap<Enchantment, Int>)
                EnchantTools.updateLore(this)
            }
            inventory.item = itemStack
            if (enchantsToAdd != translateEnchantByChance) {
                event.enchanter.sendColorMessage(Message.enchant)
            }
        }
    }
}