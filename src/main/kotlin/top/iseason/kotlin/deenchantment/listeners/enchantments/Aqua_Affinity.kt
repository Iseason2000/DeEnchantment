package top.iseason.kotlin.deenchantment.listeners.enchantments

import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityAirChangeEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import top.iseason.kotlin.deenchantment.manager.DeEnchantment

//水下慢掘
class Aqua_Affinity : Listener {
    @EventHandler
    fun onEntityAirChangeEvent(event: EntityAirChangeEvent) {
        if (event.isCancelled) return
        val entity = event.entity as LivingEntity
        if (entity !is Player) return //挖掘仅限玩家
        if (entity.eyeLocation.block.type != Material.WATER) return //头部在水中
        val helmet = entity.equipment?.helmet ?: return
        val enchantments = helmet.enchantments
        val level = enchantments[DeEnchantment.DE_aqua_affinity] ?: return
        entity.addPotionEffect(PotionEffect(PotionEffectType.SLOW_DIGGING, 2, level - 1))

    }
}