package top.iseason.kotlin.deenchantment.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.LootGenerateEvent

class LootGenerateListener  : Listener {
    @EventHandler
    fun onLootGenerateEvent(event: LootGenerateEvent){
        println(event.lootContext.location)
    }
}