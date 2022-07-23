package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityAirChangeEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments

//水下窒息
object Respiration : BaseEnchant(DeEnchantments.DE_respiration) {
    // 资源占用较高
    @EventHandler
    fun onEntityAirChangeEvent(event: EntityAirChangeEvent) {
        if (event.isCancelled) return
        val entity = event.entity
        if (entity !is Player) return //只限玩家
        if (entity.eyeLocation.block.type != Material.WATER) return
        val helmet = entity.equipment?.helmet ?: return
        val enchantments = helmet.enchantments
        val level = enchantments[DeEnchantments.DE_respiration] ?: return
        val amount = event.amount
        if (amount <= -20 - level * 20) {
            event.amount = 0
            entity.damage(2.0)
        } else
            event.amount = amount - level
    }
}