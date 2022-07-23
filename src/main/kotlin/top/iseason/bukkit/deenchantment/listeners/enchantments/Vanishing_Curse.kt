package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.Bukkit
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.ItemDespawnEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments

//永存祝福
object Vanishing_Curse : BaseEnchant(DeEnchantments.DE_vanishing_curse) {
    @EventHandler
    fun onItemDeSpawnEvent(event: ItemDespawnEvent) {
        if (event.isCancelled) return
        val level = event.entity.itemStack.enchantments[DeEnchantments.DE_vanishing_curse] ?: return
        if (level <= 0) return
        event.isCancelled = true
    }

    @EventHandler
    fun onEntityDamageEvent(event: EntityDamageEvent) {
        val entity = event.entity
        if (entity !is Item) return
        val itemStack = entity.itemStack
        val level = itemStack.enchantments[DeEnchantments.DE_vanishing_curse] ?: return
        if (level <= 0) return
        event.isCancelled = true
        val uuid = entity.thrower ?: return
        val thrower = Bukkit.getEntity(uuid) ?: return
        entity.teleport(thrower)
    }
}