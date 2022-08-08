package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerItemDamageEvent
import top.iseason.bukkit.bukkittemplate.config.annotations.Comment
import top.iseason.bukkit.bukkittemplate.config.annotations.Key
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments

//易损
object UnBreaking : BaseEnchant(DeEnchantments.DE_unbreaking) {
    @Key
    @Comment("", "耐久损耗等级乘数")
    var damageRate = 1

    @EventHandler(ignoreCancelled = true)
    fun onPlayerItemDamageEvent(event: PlayerItemDamageEvent) {
        val level = event.item.getDeLevel()
        if (level <= 0) return
        if (!checkPermission(event.player)) return
        event.damage = event.damage + level * damageRate
    }
}