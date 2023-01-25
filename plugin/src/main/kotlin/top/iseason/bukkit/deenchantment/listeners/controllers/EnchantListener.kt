package top.iseason.bukkit.deenchantment.listeners.controllers

import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.inventory.EnchantingInventory
import top.iseason.bukkit.deenchantment.settings.Message
import top.iseason.bukkit.deenchantment.utils.EnchantTools
import top.iseason.bukkittemplate.utils.bukkit.ItemUtils.applyMeta
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.sendColorMessage
import top.iseason.bukkittemplate.utils.other.submit

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