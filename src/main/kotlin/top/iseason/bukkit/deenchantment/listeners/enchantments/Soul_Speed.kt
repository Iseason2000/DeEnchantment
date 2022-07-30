package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.potion.PotionEffectType
import top.iseason.bukkit.bukkittemplate.utils.submit
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkit.deenchantment.runnables.PotionAdder
import java.util.*
//TODO: 重构
object Soul_Speed : BaseEnchant(DeEnchantments.DE_soul_speed) {
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
        val level = enchantments[DeEnchantments.DE_soul_speed]
        if (level == null || level <= 0) {
            val potionAdder = playerMap[uniqueId] ?: return
            potionAdder.cancel()
            playerMap.remove(uniqueId)
            return
        }
        if (playerMap.containsKey(uniqueId)) return
        val runTaskTimer = PotionAdder(player, PotionEffectType.SPEED, 220, level)
        submit(period = 200, task = runTaskTimer)
        playerMap[uniqueId] = runTaskTimer
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        val uniqueId = event.player.uniqueId
        val potionAdder = playerMap[uniqueId] ?: return
        potionAdder.cancel()
        playerMap.remove(uniqueId)
    }

    @EventHandler
    fun onPlayerDeathEvent(event: PlayerDeathEvent) {
        val uniqueId = event.entity.uniqueId
        val potionAdder = playerMap[uniqueId] ?: return
        potionAdder.cancel()
        playerMap.remove(uniqueId)
    }
}