package top.iseason.kotlin.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.potion.PotionEffectType
import top.iseason.kotlin.deenchantment.manager.ConfigManager
import top.iseason.kotlin.deenchantment.manager.DeEnchantment
import top.iseason.kotlin.deenchantment.utils.runnables.PotionAdder
import java.util.*

class Soul_Speed : Listener {
    private val playerMap = HashMap<UUID, PotionAdder>()

    @EventHandler
    fun onPlayerMoveEvent(event: PlayerMoveEvent) {
        if (event.isCancelled) return
        val player = event.player
        val boots = player.equipment?.boots ?: return
        val uniqueId = player.uniqueId
        val enchantments = boots.enchantments
        val level = enchantments[DeEnchantment.DE_soul_speed]
        if (level == null && playerMap.containsKey(uniqueId)) {
            playerMap[uniqueId]?.cancel()
            playerMap.remove(uniqueId)
            return
        }
        if (level == null) return
        if (level <= 0) return
        if (playerMap.containsKey(uniqueId)) return
        val runTaskTimer = PotionAdder(player, PotionEffectType.SPEED, 220, level)
        runTaskTimer.runTaskTimer(ConfigManager.getPlugin(), 0L, 200L)
        playerMap[uniqueId] = runTaskTimer
    }
}