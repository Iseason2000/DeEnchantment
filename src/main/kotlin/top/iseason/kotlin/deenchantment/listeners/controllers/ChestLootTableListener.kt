package top.iseason.kotlin.deenchantment.listeners.controllers

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.world.LootGenerateEvent
import top.iseason.kotlin.deenchantment.utils.EnchantTools

class ChestLootTableListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onLootGenerateEvent(event: LootGenerateEvent) {
        if (event.isCancelled) return
        val loot = event.loot
        for (item in loot) {
            EnchantTools.translateEnchantsByChance(item)
        }
    }
}