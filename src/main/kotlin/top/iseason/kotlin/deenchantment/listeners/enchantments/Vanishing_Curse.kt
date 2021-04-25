package top.iseason.kotlin.deenchantment.listeners.enchantments

import org.bukkit.Bukkit
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.ItemDespawnEvent
import top.iseason.kotlin.deenchantment.manager.DeEnchantment

//永存祝福
class Vanishing_Curse : Listener {
    @EventHandler
    fun onItemDeSpawnEvent(event: ItemDespawnEvent) {
        if (event.isCancelled) return
        val level = event.entity.itemStack.enchantments[DeEnchantment.DE_vanishing_curse] ?: return
        if (level <= 0) return
        event.isCancelled = true
    }

    @EventHandler
    fun onEntityDamageEvent(event: EntityDamageEvent) {
        val entity = event.entity
        if (entity !is Item) return
        val itemStack = entity.itemStack
        val level = itemStack.enchantments[DeEnchantment.DE_vanishing_curse] ?: return
        if (level <= 0) return
        event.isCancelled = true
        val uuid = entity.thrower ?: return
        val thrower = Bukkit.getEntity(uuid) ?: return
        entity.teleport(thrower)
    }
}