package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerItemDamageEvent
import top.iseason.bukkit.bukkittemplate.config.annotations.Comment
import top.iseason.bukkit.bukkittemplate.config.annotations.Key
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments

object Mending : BaseEnchant(DeEnchantments.DE_mending) {
    @Key
    @Comment("", "经验等级乘数")
    var expRate = 1

    @EventHandler
    fun onPlayerItemDamageEvent(event: PlayerItemDamageEvent) {
        if (event.isCancelled) return
        val level = event.item.getDeLevel()
        if (level <= 0) return
        if (!checkPermission(event.player as? Player)) return
        if (event.damage < 1) return
        val player = event.player
        player.giveExp(level * expRate)
    }
}