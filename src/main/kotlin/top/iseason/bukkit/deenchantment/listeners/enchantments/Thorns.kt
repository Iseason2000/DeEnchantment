package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import top.iseason.bukkit.bukkittemplate.utils.RandomUtils
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkit.deenchantment.utils.EnchantTools

//负荆请罪
object Thorns : BaseEnchant(DeEnchantments.DE_thorns) {
    @EventHandler
    fun onPlayerMoveEvent(event: PlayerMoveEvent) {
        if (event.isCancelled) return
        val player = event.player
        val levelCount = EnchantTools.getLevelCount(player, DeEnchantments.DE_thorns)
        if (levelCount == 0) return
        if (RandomUtils.getDouble() < levelCount * 0.005) {
            player.damage(0.5 * levelCount)
        }
    }
}