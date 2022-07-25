package top.iseason.bukkit.deenchantment.listeners.controllers

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.world.LootGenerateEvent
import top.iseason.bukkit.deenchantment.utils.EnchantTools

class ChestLootTableListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onLootGenerateEvent(event: LootGenerateEvent) {
        val loot = event.loot
        for (item in loot) {
            EnchantTools.translateEnchantsByChance(item)
        }
    }
}