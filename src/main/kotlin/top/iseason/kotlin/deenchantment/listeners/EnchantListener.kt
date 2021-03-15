package top.iseason.kotlin.deenchantment.listeners

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import top.iseason.kotlin.deenchantment.manager.ConfigManager
import top.iseason.kotlin.deenchantment.utils.EnchantTools

class EnchantListener : Listener {
    @EventHandler
    fun onEnchantItemEvent(event: EnchantItemEvent) {
        if (event.isCancelled) return
        var item = event.item
        val enchantsToAdd = event.enchantsToAdd
        if (item.type == Material.BOOK) {
            item = ItemStack(Material.ENCHANTED_BOOK)
            EnchantTools.translateEnchantsByChance(item, enchantsToAdd)
        } else
            EnchantTools.translateEnchantsByChance(item, enchantsToAdd)
        event.enchantsToAdd.clear()//todo:不会刷新附魔种子
        event.inventory.setItem(0, item)
    }
}