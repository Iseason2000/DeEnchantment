package top.iseason.kotlin.deenchantment.listeners.controllers

import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import top.iseason.kotlin.deenchantment.utils.EnchantTools

class EntitySpawnListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onCreatureSpawnEvent(event: CreatureSpawnEvent) {
        if (event.isCancelled) return
        val entity = event.entity
        if (entity !is Mob) return
        val equipment = entity.equipment ?: return
        val armorContents = equipment.armorContents
        for (armor in armorContents) {
            if (armor == null || !armor.hasItemMeta()) continue
            EnchantTools.translateEnchantsByChance(armor)
        }
        val itemInMainHand = equipment.itemInMainHand
        if (itemInMainHand.hasItemMeta()) {
            EnchantTools.translateEnchantsByChance(itemInMainHand)
            equipment.setItemInMainHand(itemInMainHand)
        }
        val itemInOffHand = equipment.itemInOffHand
        if (itemInOffHand.hasItemMeta()) {
            EnchantTools.translateEnchantsByChance(itemInOffHand)
            equipment.setItemInOffHand(itemInOffHand)
        }

    }
}