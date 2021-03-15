package top.iseason.kotlin.deenchantment.listeners

import org.bukkit.entity.EntityType
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent

class PlayerFishListener : Listener {
    @EventHandler
    fun onPlayerFishEvent(event: PlayerFishEvent) {
        if(event.isCancelled) return
        val caught = event.caught
        println(caught?.type?.name)
        if(caught !is Item) return
        println("This is a Item")
        val itemStack = caught.itemStack

    }
}