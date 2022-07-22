package top.iseason.bukkit.deenchantment.listeners.controllers

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDropItemEvent
import top.iseason.bukkit.deenchantment.utils.EnchantTools

class EntityDropItemListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onEntityDropItemEvent(event: EntityDropItemEvent) {
        if (event.isCancelled) return
        val itemDrop = event.itemDrop
        val itemStack = itemDrop.itemStack
        EnchantTools.translateEnchantsByChance(itemStack)
    }
}