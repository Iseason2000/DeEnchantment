package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.scheduler.BukkitRunnable
import top.iseason.bukkit.bukkittemplate.config.annotations.Comment
import top.iseason.bukkit.bukkittemplate.config.annotations.Key
import top.iseason.bukkit.bukkittemplate.utils.RandomUtils
import top.iseason.bukkit.bukkittemplate.utils.submit
import top.iseason.bukkit.deenchantment.events.DePlayerEquipmentChangeEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments

//负荆请罪
object Thorns : BaseEnchant(DeEnchantments.DE_thorns) {
    @Key
    @Comment("", "触发概率等级乘数")
    var chanceRate = 0.05

    @Key
    @Comment("", "伤害等级乘数")
    var damageRate = 0.5


    private val playerMap = HashMap<Player, BukkitRunnable>()

    @EventHandler(ignoreCancelled = true)
    fun onDePlayerEquipmentChange(event: DePlayerEquipmentChangeEvent) {
        val deLevel = event.getDeLevel()
        val player = event.player
        playerMap[player]?.cancel()
        playerMap.remove(player)
        if (deLevel == 0) return
        val thornsRunnable = ThornsRunnable(event.player, deLevel)
        submit(period = 10, async = true, task = thornsRunnable)
        playerMap[player] = thornsRunnable
    }

    class ThornsRunnable(val player: Player, level: Int) : BukkitRunnable() {
        private val chance = chanceRate * level
        private val damage = damageRate * level
        override fun run() {
            if (!player.isOnline) {
                cancel()
                playerMap.remove(player)
                return
            }
            if (!player.isSprinting) return
            if (RandomUtils.getDouble() < chance) {
                submit {
                    player.damage(damage, player)
                }
            }
        }

    }
}