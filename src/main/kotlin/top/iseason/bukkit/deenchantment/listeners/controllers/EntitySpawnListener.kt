package top.iseason.bukkit.deenchantment.listeners.controllers

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import top.iseason.bukkit.bukkittemplate.utils.bukkit.checkAir
import top.iseason.bukkit.bukkittemplate.utils.submit
import top.iseason.bukkit.deenchantment.utils.EnchantTools

object EntitySpawnListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onCreatureSpawnEvent(event: CreatureSpawnEvent) {
        if (event.isCancelled) return
        val entity = event.entity
//        if (entity !is Mob) return
        submit(async = true) {
            val equipment = entity.equipment ?: return@submit
            val armorContents = equipment.armorContents
            for (armor in armorContents) {
                if (armor == null || armor.type.checkAir()) continue
                EnchantTools.translateEnchantsByChance(armor)
            }
            val itemInMainHand = equipment.itemInMainHand
            if (!itemInMainHand.type.checkAir()) {
                EnchantTools.translateEnchantsByChance(itemInMainHand)
                equipment.setItemInMainHand(itemInMainHand)
            }
            val itemInOffHand = equipment.itemInOffHand
            if (!itemInOffHand.type.checkAir()) {
                EnchantTools.translateEnchantsByChance(itemInOffHand)
                equipment.setItemInOffHand(itemInOffHand)
            }
        }
    }
}