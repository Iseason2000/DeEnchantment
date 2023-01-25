package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityAirChangeEvent
import top.iseason.bukkit.deenchantment.events.DePlayerEquipmentChangeEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkittemplate.config.annotations.Comment
import top.iseason.bukkittemplate.config.annotations.Key
import java.lang.Integer.max

//水下窒息
object Respiration : BaseEnchant(DeEnchantments.DE_respiration) {
    @Key
    @Comment("", "氧气消耗倍率等级乘数")
    var speedRate = 1

    val map = mutableMapOf<Player, Int>()

    @EventHandler(ignoreCancelled = true)
    fun onDePlayerEquipmentChange(event: DePlayerEquipmentChangeEvent) {
        val deLevel = event.getDeLevel()
        if (deLevel == 0) {
            map.remove(event.player)
        } else {
            if (!checkPermission(event.player)) return
            map[event.player] = deLevel
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onEntityAirChangeEvent(event: EntityAirChangeEvent) {
        val player = event.entity as? Player ?: return
        if (!(player.gameMode == GameMode.SURVIVAL || player.gameMode == GameMode.ADVENTURE)) return
        val level = map[player] ?: return
        if (player.eyeLocation.block.type != Material.WATER) return
        val amount = event.amount - speedRate * level
        event.amount = max(amount, -20)
    }
}