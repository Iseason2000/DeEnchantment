package top.iseason.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.potion.PotionEffectType
import top.iseason.deenchantment.manager.ConfigManager
import top.iseason.deenchantment.manager.DeEnchantment
import top.iseason.deenchantment.utils.runnables.PotionAdder
import java.util.*

//旱鸭子 只能改变视野
class Depth_Strider : Listener {
    private val playerMap = HashMap<UUID, PotionAdder>()

    @EventHandler
    fun onPlayerMoveEvent(event: PlayerMoveEvent) {
        if (event.isCancelled) return
        val player = event.player
        val uniqueId = player.uniqueId
        var potionAdder = playerMap[uniqueId]
        if (!player.isInWater) {//脚部在水中
            potionAdder?.cancel()
            return
        }
        if (potionAdder != null)
            return
        val boots = player.equipment?.boots ?: return
        val enchantments = boots.enchantments
        val level = enchantments[DeEnchantment.DE_depth_strider] ?: return
        if (level == 0) return
        potionAdder = PotionAdder(player, PotionEffectType.SLOW, 220, level)
        potionAdder.runTaskTimer(ConfigManager.getPlugin(), 0L, 200L)
        playerMap[uniqueId] = potionAdder
    }
}