package top.iseason.bukkit.deenchantment.listeners.controllers

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDropItemEvent
import top.iseason.bukkit.deenchantment.utils.EnchantTools

object EntityDropItemListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityDropItemEvent(event: EntityDropItemEvent) {
        val itemDrop = event.itemDrop
        val itemStack = itemDrop.itemStack
        EnchantTools.translateEnchantsByChance(itemStack)
    }
}