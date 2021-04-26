package top.iseason.kotlin.deenchantment.listeners.enchantments

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.potion.PotionEffectType
import top.iseason.kotlin.deenchantment.manager.ConfigManager
import top.iseason.kotlin.deenchantment.manager.DeEnchantment
import top.iseason.kotlin.deenchantment.utils.runnables.PotionAdder

//水下慢掘
class Aqua_Affinity : Listener {
    private val playerMap = HashMap<Player, PotionAdder>()

    @EventHandler
    fun onEntityAirChangeEvent(event: BlockDamageEvent) {
        if (event.isCancelled) return
        val player = event.player
        if (player.eyeLocation.block.type != Material.WATER) {
            if (playerMap.containsKey(player)) {
                val potionAdder = playerMap[player] ?: return
                potionAdder.cancel()
                player.removePotionEffect(PotionEffectType.SLOW_DIGGING)
                playerMap.remove(player)
            }
            return
        }
        val helmet = player.equipment?.helmet
        if (helmet == null) {
            if (playerMap.containsKey(player)) {
                val potionAdder = playerMap[player] ?: return
                potionAdder.cancel()
                player.removePotionEffect(PotionEffectType.SLOW_DIGGING)
                playerMap.remove(player)
            }
            return
        }
        val enchantments = helmet.enchantments
        val level = enchantments[DeEnchantment.DE_aqua_affinity]
        if (level == null) {
            val potionAdder = playerMap[player] ?: return
            potionAdder.cancel()
            player.removePotionEffect(PotionEffectType.SLOW_DIGGING)
            playerMap.remove(player)
            return
        }
        val potionAdder = PotionAdder(player, PotionEffectType.SLOW_DIGGING, 120, level)
        potionAdder.runTaskTimer(ConfigManager.getPlugin(), 0L, 100L)
        playerMap[player] = potionAdder
    }
}