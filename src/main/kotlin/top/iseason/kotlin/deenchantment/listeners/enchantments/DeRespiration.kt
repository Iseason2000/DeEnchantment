package top.iseason.kotlin.deenchantment.listeners.enchantments

import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.WaterMob
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityAirChangeEvent
import top.iseason.kotlin.deenchantment.manager.DeEnchantment

//水下窒息
class DeRespiration : Listener {
    // 不完美
    @EventHandler
    fun onEntityAirChangeEvent(event: EntityAirChangeEvent) {
        if (event.isCancelled) return
        val entity = event.entity as LivingEntity
        if (entity is WaterMob) return //水生生物不会窒息
        if (entity.eyeLocation.block.type != Material.WATER) return
        val helmet = entity.equipment?.helmet ?: return
        val enchantments = helmet.enchantments
        val level = enchantments[DeEnchantment.DE_respiration] ?: return
        val amount = event.amount
        if (amount <= -20 - level * 20) {
            event.amount = 0
            entity.damage(2.0)
        } else
            event.amount = amount - level
    }
}