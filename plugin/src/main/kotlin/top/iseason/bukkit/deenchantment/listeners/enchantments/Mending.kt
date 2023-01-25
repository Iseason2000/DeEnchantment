package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerItemDamageEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkittemplate.config.annotations.Comment
import top.iseason.bukkittemplate.config.annotations.Key

object Mending : BaseEnchant(DeEnchantments.DE_mending) {

    @Key
    @Comment("", "经验等级乘数")
    var expRate = 1.0

    @Key
    @Comment("", "额外耐久等级乘数")
    var damageRate = 1.0

    @EventHandler
    fun onPlayerItemDamageEvent(event: PlayerItemDamageEvent) {
        if (event.isCancelled) return
        val level = event.item.getDeLevel()
        if (level <= 0) return
        if (!checkPermission(event.player as? Player)) return
        if (event.damage < 1) return
        event.damage += (level * damageRate).toInt()
        val player = event.player
        player.giveExp((level * expRate).toInt())
    }
}