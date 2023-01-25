package top.iseason.bukkit.deenchantment.listeners.controllers

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import top.iseason.bukkit.deenchantment.utils.EnchantTools
import top.iseason.bukkittemplate.utils.bukkit.ItemUtils.checkAir
import top.iseason.bukkittemplate.utils.other.submit

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
                if (armor.checkAir()) continue
                EnchantTools.translateEnchantsByChance(armor)
            }
            val itemInMainHand = equipment.itemInMainHand
            if (!itemInMainHand.checkAir()) {
                EnchantTools.translateEnchantsByChance(itemInMainHand)
                equipment.setItemInMainHand(itemInMainHand)
            }
            val itemInOffHand = equipment.itemInOffHand
            if (!itemInOffHand.checkAir()) {
                EnchantTools.translateEnchantsByChance(itemInOffHand)
                equipment.setItemInOffHand(itemInOffHand)
            }
        }
    }
}