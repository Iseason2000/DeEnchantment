package top.iseason.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.potion.PotionEffectType
import top.iseason.deenchantment.manager.ConfigManager
import top.iseason.deenchantment.manager.DeEnchantment
import top.iseason.deenchantment.utils.runnables.PotionAdder
import java.util.*

class Soul_Speed : Listener {
    private val playerMap = HashMap<UUID, PotionAdder>()

    @EventHandler
    fun onPlayerMoveEvent(event: PlayerMoveEvent) {
        if (event.isCancelled) return
        val player = event.player
        val boots = player.equipment?.boots
        val uniqueId = player.uniqueId
        if (boots == null) {
            val potionAdder = playerMap[uniqueId] ?: return
            potionAdder.cancel()
            playerMap.remove(uniqueId)
            return
        }
        val enchantments = boots.enchantments
        val level = enchantments[DeEnchantment.DE_soul_speed]
        if (level == null || level <= 0) {
            val potionAdder = playerMap[uniqueId] ?: return
            potionAdder.cancel()
            playerMap.remove(uniqueId)
            return
        }
        if (playerMap.containsKey(uniqueId)) return
        val runTaskTimer = PotionAdder(player, PotionEffectType.SPEED, 220, level)
        runTaskTimer.runTaskTimer(ConfigManager.getPlugin(), 0L, 200L)
        playerMap[uniqueId] = runTaskTimer
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        playerMap.remove(event.player.uniqueId)
    }

    @EventHandler
    fun onPlayerDeathEvent(event: PlayerDeathEvent) {
        playerMap.remove(event.entity.uniqueId)
    }
}