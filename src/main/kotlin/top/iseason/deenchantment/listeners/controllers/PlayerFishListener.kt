package top.iseason.deenchantment.listeners.controllers

import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent
import top.iseason.deenchantment.utils.EnchantTools

class PlayerFishListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerFishEvent(event: PlayerFishEvent) {
        if(event.isCancelled) return
        if(event.state != PlayerFishEvent.State.CAUGHT_FISH) return
        val caught = event.caught?:return
        if(caught !is Item) return
        val itemStack = caught.itemStack
        EnchantTools.translateEnchantsByChance(itemStack)
    }
}