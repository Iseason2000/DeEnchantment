package top.iseason.deenchantment.listeners.enchantments

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.potion.PotionEffectType
import top.iseason.deenchantment.manager.ConfigManager
import top.iseason.deenchantment.manager.DeEnchantment
import top.iseason.deenchantment.utils.runnables.PotionAdder
import java.util.*

//水下慢掘
class Aqua_Affinity : Listener {
    private val playerMap = HashMap<UUID, PotionAdder>()

    @EventHandler
    fun onEntityAirChangeEvent(event: BlockDamageEvent) {
        if (event.isCancelled) return
        val player = event.player
        val uniqueId = player.uniqueId
        if (player.eyeLocation.block.type != Material.WATER) {
            if (playerMap.containsKey(uniqueId)) {
                val potionAdder = playerMap[uniqueId] ?: return
                potionAdder.cancel()
                player.removePotionEffect(PotionEffectType.SLOW_DIGGING)
                playerMap.remove(uniqueId)
            }
            return
        }
        val helmet = player.equipment?.helmet
        if (helmet == null) {
            if (playerMap.containsKey(uniqueId)) {
                val potionAdder = playerMap[uniqueId] ?: return
                potionAdder.cancel()
                player.removePotionEffect(PotionEffectType.SLOW_DIGGING)
                playerMap.remove(uniqueId)
            }
            return
        }
        val enchantments = helmet.enchantments
        val level = enchantments[DeEnchantment.DE_aqua_affinity]
        if (level == null) {
            val potionAdder = playerMap[uniqueId] ?: return
            potionAdder.cancel()
            player.removePotionEffect(PotionEffectType.SLOW_DIGGING)
            playerMap.remove(uniqueId)
            return
        }
        val potionAdder = PotionAdder(player, PotionEffectType.SLOW_DIGGING, 120, level)
        potionAdder.runTaskTimer(ConfigManager.getPlugin(), 0L, 100L)
        playerMap[uniqueId] = potionAdder
    }
}