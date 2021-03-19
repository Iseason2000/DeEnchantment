package top.iseason.kotlin.deenchantment.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.LootGenerateEvent
import top.iseason.kotlin.deenchantment.utils.EnchantTools

class ChestLootTableListener : Listener {
    @EventHandler
    fun onLootGenerateEvent(event: LootGenerateEvent) {
        val loot = event.loot
        for (item in loot) {
            EnchantTools.translateEnchantsByChance(item)
        }
    }
}