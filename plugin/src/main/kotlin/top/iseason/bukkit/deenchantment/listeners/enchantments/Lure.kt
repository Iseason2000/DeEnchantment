package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerFishEvent
import top.iseason.bukkit.deenchantment.events.DePlayerFishEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkittemplate.config.annotations.Comment
import top.iseason.bukkittemplate.config.annotations.Key

//过期钓饵
object Lure : BaseEnchant(DeEnchantments.DE_lure) {
    @Key
    @Comment("", "平均等待时间百分比等级乘数")
    var waitTimeRate = 0.2

    @EventHandler(ignoreCancelled = true)
    fun onPlayerFishEvent(event: DePlayerFishEvent) {
        val fishEvent = event.event
        if (fishEvent.state != PlayerFishEvent.State.FISHING) return
        val level = event.getDeLevel()
        if (level <= 0) return
        if (!checkPermission(event.player as? Player)) return
        val hook = fishEvent.hook
        hook.maxWaitTime = (hook.maxWaitTime * (waitTimeRate * level + 1)).toInt()
        hook.minWaitTime = (hook.minWaitTime * (waitTimeRate * level + 1)).toInt()
    }
}