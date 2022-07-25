package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.potion.PotionEffectType
import top.iseason.bukkit.bukkittemplate.utils.submit
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkit.deenchantment.runnables.PotionAdder
import java.util.*

//水下慢掘
object Aqua_Affinity : BaseEnchant(DeEnchantments.DE_aqua_affinity) {
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
        val level = enchantments[DeEnchantments.DE_aqua_affinity]
        if (level == null) {
            val potionAdder = playerMap[uniqueId] ?: return
            potionAdder.cancel()
            player.removePotionEffect(PotionEffectType.SLOW_DIGGING)
            playerMap.remove(uniqueId)
            return
        }
        val potionAdder = PotionAdder(player, PotionEffectType.SLOW_DIGGING, 120, level)
        submit(delay = 100L, task = PotionAdder(player, PotionEffectType.SLOW_DIGGING, 120, level))
        playerMap[uniqueId] = potionAdder
    }
}