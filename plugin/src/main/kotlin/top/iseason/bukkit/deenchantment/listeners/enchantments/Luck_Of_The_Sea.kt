package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerFishEvent
import top.iseason.bukkit.deenchantment.events.DePlayerFishEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkittemplate.config.annotations.Comment
import top.iseason.bukkittemplate.config.annotations.Key
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.sendColorMessage
import top.iseason.bukkittemplate.utils.other.RandomUtils

//海之嫌弃
object Luck_Of_The_Sea : BaseEnchant(DeEnchantments.DE_luck_of_the_sea) {
    @Key
    @Comment("", "触发概率等级乘数")
    var chanceRate: Double = 0.1

    @Key
    @Comment("", "触发消息")
    var message = "&6你受到了大海的嫌弃并回收了你的东西"

    @EventHandler(ignoreCancelled = true)
    fun onPlayerFishEvent(event: DePlayerFishEvent) {
        val fishEvent = event.event
        if (fishEvent.state != PlayerFishEvent.State.CAUGHT_FISH) return
        val level = event.getDeLevel()
        if (level <= 0) return
        if (!checkPermission(event.player as? Player)) return
        if (RandomUtils.getDouble() > level * chanceRate) return
        fishEvent.caught?.remove() ?: return
        event.player.sendColorMessage(message)
    }
}