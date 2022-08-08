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
    @EventHandler(ignoreCancelled = true)
    fun onItemDeSpawnEvent(event: ItemDespawnEvent) {
        val level = event.entity.itemStack.getDeLevel()
        if (level <= 0) return
        val owner = event.entity.owner
        if (owner != null && !checkPermission(Bukkit.getPlayer(owner))) return
        event.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun onEntityDamageEvent(event: EntityDamageEvent) {
        val item = event.entity as? Item ?: return
        val itemStack = item.itemStack
        val level = itemStack.getDeLevel()
        if (level <= 0) return
        val owner = item.owner
        if (owner != null && !checkPermission(Bukkit.getPlayer(owner))) return
        event.isCancelled = true
        val uuid = item.thrower ?: return
        val thrower = Bukkit.getEntity(uuid) ?: return
        item.teleport(thrower)
    }
}