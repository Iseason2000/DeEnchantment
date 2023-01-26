package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.scheduler.BukkitRunnable
import top.iseason.bukkit.deenchantment.events.DePlayerEquipmentChangeEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkittemplate.config.annotations.Comment
import top.iseason.bukkittemplate.config.annotations.Key
import top.iseason.bukkittemplate.utils.other.RandomUtils
import top.iseason.bukkittemplate.utils.other.submit

//负荆请罪
object Thorns : BaseEnchant(DeEnchantments.DE_thorns) {
    @Key
    @Comment("", "触发概率等级乘数")
    var chanceRate = 0.05

    @Key
    @Comment("", "伤害等级乘数")
    var damageRate = 0.5


    private val playerMap = HashMap<Player, ThornsRunnable>()

    @EventHandler(ignoreCancelled = true)
    fun onDePlayerEquipmentChange(event: DePlayerEquipmentChangeEvent) {
        val deLevel = event.getDeLevel()
        val player = event.player
        if (deLevel == 0 || !checkPermission(event.player)) {
            val thornsRunnable = playerMap[player] ?: return
            playerMap.remove(player)
            thornsRunnable.cancel()
            return
        }
        val runnable = playerMap.computeIfAbsent(player) {
            ThornsRunnable(event.player, deLevel).also { runnable ->
                submit(period = 10, async = true, task = runnable)
            }
        }
        runnable.setLevel(deLevel)
    }

    private class ThornsRunnable(val player: Player, level: Int) : BukkitRunnable() {
        private var chance = chanceRate * level
        private var damage = damageRate * level
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

        fun setLevel(level: Int) {
            chance = chanceRate * level
            damage = damageRate * level
        }

        override fun cancel() {
            if (isCancelled) return
            super.cancel()
        }
    }
}