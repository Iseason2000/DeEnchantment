package top.iseason.bukkit.deenchantment.runnables

import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

class CrossBowCanceller(val player: Player, private val hand: EquipmentSlot, val item: ItemStack) : BukkitRunnable() {
    override fun run() {
        player.equipment?.setItem(hand, item)
    }
}