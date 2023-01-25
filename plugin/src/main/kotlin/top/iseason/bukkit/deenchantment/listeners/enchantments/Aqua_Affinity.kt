package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkittemplate.config.annotations.Comment
import top.iseason.bukkittemplate.config.annotations.Key
import top.iseason.bukkittemplate.utils.other.RandomUtils

//水下慢掘
object Aqua_Affinity : BaseEnchant(DeEnchantments.DE_aqua_affinity) {
    @Key
    @Comment("", "每级挖掘失败的概率系数")
    var rate = 10.0

    @EventHandler(ignoreCancelled = true)
    fun onBlockDamageEvent(event: BlockBreakEvent) {
        val player = event.player
        if (player.eyeLocation.block.type != Material.WATER) return
        if (!checkPermission(player)) return
        val level = player.getArmorDeEnchant()
        if (level == 0) return
        if (!RandomUtils.checkPercentage(rate * level)) {
            event.isCancelled = true
        }
    }
}