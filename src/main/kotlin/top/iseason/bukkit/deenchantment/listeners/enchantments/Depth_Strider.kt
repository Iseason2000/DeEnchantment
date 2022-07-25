package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.potion.PotionEffectType
import top.iseason.bukkit.bukkittemplate.utils.submit
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkit.deenchantment.runnables.PotionAdder
import java.util.*

//旱鸭子 只能改变视野
object Depth_Strider : BaseEnchant(DeEnchantments.DE_depth_strider) {
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
        val level = enchantments[DeEnchantments.DE_depth_strider] ?: return
        if (level == 0) return
        potionAdder = PotionAdder(player, PotionEffectType.SLOW, 220, level)
        submit(period = 200L, task = potionAdder)
        playerMap[uniqueId] = potionAdder
    }
}