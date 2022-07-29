package top.iseason.bukkit.deenchantment.events

import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerFishEvent
import top.iseason.bukkit.deenchantment.manager.DeEnchantmentWrapper

class DePlayerFishEvent(
    val player: Player,
    val event: PlayerFishEvent
) : DeEnchantmentEvent(player, false) {

    override fun setCancelled(cancel: Boolean) {
        super.setCancelled(cancel)
        event.isCancelled = cancel
    }

    override fun getDeEnchantLevel(en: DeEnchantmentWrapper): Int {
        if (deEnchantments == null) {
            deEnchantments = mutableMapOf()
            player.inventory.itemInMainHand.enchantments.forEach { (t, u) ->
                if (t !is DeEnchantmentWrapper) return@forEach
                deEnchantments!![t] = u
            }
            player.inventory.itemInOffHand.enchantments.forEach { (t, u) ->
                if (t !is DeEnchantmentWrapper) return@forEach
                val i = deEnchantments!![t] ?: 0
                deEnchantments!![t] = i + u
            }
        }
        return deEnchantments!![en] ?: 0
    }
}