package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.potion.PotionEffectType
import top.iseason.bukkit.deenchantment.events.DePlayerEquipmentChangeEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkit.deenchantment.runnables.PotionAdder
import top.iseason.bukkittemplate.config.annotations.Comment
import top.iseason.bukkittemplate.config.annotations.Key
import top.iseason.bukkittemplate.utils.other.submit
import java.util.*

//旱鸭子 只能改变视野
object Depth_Strider : BaseEnchant(DeEnchantments.DE_depth_strider) {
    @Key
    @Comment("", "缓慢等级乘数,越高水下视野越低(通过给缓慢药水实现)")
    var rate = 2
    private val playerMap = HashMap<UUID, PotionAdder>()
    private val hasLevel = HashMap<UUID, Int>()

    @EventHandler(ignoreCancelled = true)
    fun onEquipmentChange(event: DePlayerEquipmentChangeEvent) {
        if (!checkPermission(event.player)) return
        val deEnchantLevel = event.getDeEnchantLevel(enchant)
        val uniqueId = event.player.uniqueId
        if (deEnchantLevel > 0) {
            hasLevel[uniqueId] = deEnchantLevel
        } else {
            hasLevel.remove(uniqueId)
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerMoveEvent(event: PlayerMoveEvent) {
        if (event.isCancelled) return
        val player = event.player
        val uniqueId = player.uniqueId
        val level = hasLevel[uniqueId]
        var potionAdder = playerMap[uniqueId]
        if (level == null || player.eyeLocation.block.type != Material.WATER) {
            potionAdder?.cancel()
            playerMap.remove(uniqueId)
            return
        }
        if (potionAdder != null) return
        potionAdder = PotionAdder(player, PotionEffectType.SLOW, 220, level * rate)
        submit(period = 200L, task = potionAdder)
        playerMap[uniqueId] = potionAdder
    }
}