package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.Guardian
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Trident
import org.bukkit.entity.WaterMob
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantment

//刺穿
class Impaling : BaseEnchant(DeEnchantment.DE_impaling) {
    @EventHandler
    fun onEntityDamageByEntityEvent(event: EntityDamageByEntityEvent) {
        val entity = event.entity
        val damager = event.damager
        var item: ItemStack? = null
        if (entity is WaterMob || entity is Guardian) return
        if (damager is Trident) {
            item = damager.item
        } else if (damager is LivingEntity) {
            item = damager.equipment?.itemInMainHand ?: return
        }
        if (item == null) return
        val level = item.enchantments[DeEnchantment.DE_impaling] ?: return
        if (level <= 0) return
        event.damage = event.damage * (1 + 0.25 * level)

    }
}