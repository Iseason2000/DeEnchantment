package top.iseason.kotlin.deenchantment.utils

import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

class Runable(val player: Player, val hand: EquipmentSlot, val item: ItemStack) : BukkitRunnable() {
    override fun run() {
        player.equipment?.setItem(hand, item)
    }
}