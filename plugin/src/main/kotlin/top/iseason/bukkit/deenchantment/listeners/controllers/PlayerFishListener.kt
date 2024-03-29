package top.iseason.bukkit.deenchantment.listeners.controllers

import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent
import top.iseason.bukkit.deenchantment.utils.EnchantTools

object PlayerFishListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerFishEvent(event: PlayerFishEvent) {
        if (event.state != PlayerFishEvent.State.CAUGHT_FISH) return
        val caught = event.caught ?: return
        if (caught !is Item) return
        val itemStack = caught.itemStack
        EnchantTools.translateEnchantsByChance(itemStack)
    }
}