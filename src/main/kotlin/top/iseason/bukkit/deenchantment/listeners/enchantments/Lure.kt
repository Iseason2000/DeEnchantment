package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerFishEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments

//过期钓饵
object Lure : BaseEnchant(DeEnchantments.DE_lure) {
    @EventHandler
    fun onPlayerFishEvent(event: PlayerFishEvent) {
        if (event.isCancelled) return
        if (event.state != PlayerFishEvent.State.FISHING) return
        val player = event.player
        val equipment = player.equipment ?: return
        val level = equipment.itemInMainHand.enchantments[DeEnchantments.DE_lure]
            ?: equipment.itemInOffHand.enchantments[DeEnchantments.DE_lure] ?: return
        if (level <= 0) return
        val hook = event.hook
        hook.maxWaitTime = 600 + level + 100
        hook.minWaitTime = 120
    }
}