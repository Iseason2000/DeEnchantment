package top.iseason.kotlin.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import top.iseason.kotlin.deenchantment.manager.DeEnchantment

//旱鸭子 只能改变视野
class Depth_Strider : Listener {
    @EventHandler
    fun onEntityAirChangeEvent(event: PlayerMoveEvent) {
        if (event.isCancelled) return
        val player = event.player
        if (!player.isInWater) return //脚部在水中
        val to = event.to ?: return
        val from = event.from
        if (to.x == from.x && to.y == from.y && to.z == from.z) return
        val boots = player.equipment?.boots ?: return
        val enchantments = boots.enchantments
        val level = enchantments[DeEnchantment.DE_depth_strider] ?: return
        player.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 2, level * 2))
    }
}