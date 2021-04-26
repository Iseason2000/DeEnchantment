package top.iseason.kotlin.deenchantment.listeners.enchantments

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.potion.PotionEffectType
import top.iseason.kotlin.deenchantment.manager.ConfigManager
import top.iseason.kotlin.deenchantment.manager.DeEnchantment
import top.iseason.kotlin.deenchantment.utils.runnables.PotionAdder

class Soul_Speed : Listener {
    val playerMap = HashMap<Player, PotionAdder>()

    @EventHandler
    fun onEntityAirChangeEvent(event: PlayerMoveEvent) {
        if (event.isCancelled) return
        val player = event.player
        val boots = player.equipment?.boots ?: return
        val enchantments = boots.enchantments
        val level = enchantments[DeEnchantment.DE_soul_speed]
        if (level == null && playerMap.containsKey(player)) {
            playerMap[player]?.cancel()
            playerMap.remove(player)
            return
        }
        if (level == null) return
        if (level <= 0) return
        if (playerMap.containsKey(player)) return
        val runTaskTimer = PotionAdder(player, PotionEffectType.SPEED, 220, level)
        runTaskTimer.runTaskTimer(ConfigManager.getPlugin(), 0L, 200L)
        playerMap[player] = runTaskTimer

    }
}