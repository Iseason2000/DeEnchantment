package top.iseason.kotlin.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent
import top.iseason.kotlin.deenchantment.manager.DeEnchantment

//过期钓饵
class Lure : Listener {
    @EventHandler
    fun onPlayerFishEvent(event: PlayerFishEvent) {
        if (event.isCancelled) return
        if (event.state != PlayerFishEvent.State.FISHING) return
        val player = event.player
        val equipment = player.equipment ?: return
        val level = equipment.itemInMainHand.enchantments[DeEnchantment.DE_lure]
            ?: equipment.itemInOffHand.enchantments[DeEnchantment.DE_lure] ?: return
        if (level <= 0) return
        val hook = event.hook
        hook.maxWaitTime = 600 + level + 100
        hook.minWaitTime = 120
    }
}