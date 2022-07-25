package top.iseason.bukkit.deenchantment.runnables

import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import top.iseason.bukkit.deenchantment.events.PlayerEquipmentChangeEvent
import java.util.*

/**
 * 玩家背包变化检测器
 */
object EquipmentScanner : BukkitRunnable() {

    private val playerEquipments = mutableMapOf<UUID, Array<ItemStack?>?>()
    override fun run() {
        Bukkit.getOnlinePlayers().forEach {
            val uniqueId = it.uniqueId
            val armorContents = it.equipment?.armorContents
            if (Arrays.equals(armorContents, playerEquipments[uniqueId])) {
                return
            }
            playerEquipments[uniqueId] = armorContents
            Bukkit.getPluginManager().callEvent(PlayerEquipmentChangeEvent(it))
        }
    }

}