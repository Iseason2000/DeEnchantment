package top.iseason.kotlin.deenchantment.listeners

import org.bukkit.event.Listener
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerLoginEvent

class Proction : Listener {
    @EventHandler
    fun onInventoryCloseEvent(e :PlayerLoginEvent) {
        println("111111111111111111")
    }
}