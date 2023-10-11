package top.iseason.bukkit.deenchantment.listeners.controllers

import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDropItemEvent
import top.iseason.bukkit.deenchantment.utils.EnchantTools

object EntityDropItemListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityDropItemEvent(event: EntityDropItemEvent) {
        val itemDrop = event.itemDrop
        val itemStack = itemDrop.itemStack
        val entity = event.entity
        if (entity is Mob) {
            val equipment = entity.equipment
            if (equipment != null) {
                if (equipment.itemInMainHandDropChance == 1.0F) return
                if (equipment.itemInOffHandDropChance == 1.0F) return
                if (equipment.helmetDropChance == 1.0F) return
                if (equipment.chestplateDropChance == 1.0F) return
                if (equipment.leggingsDropChance == 1.0F) return
                if (equipment.bootsDropChance == 1.0F) return
            }
        }
        EnchantTools.translateEnchantsByChance(itemStack)
    }
}