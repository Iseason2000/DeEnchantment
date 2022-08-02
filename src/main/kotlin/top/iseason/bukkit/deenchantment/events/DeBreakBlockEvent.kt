package top.iseason.bukkit.deenchantment.events

import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import top.iseason.bukkit.deenchantment.manager.DeEnchantmentWrapper

class DeBreakBlockEvent(val player: Player, val event: BlockBreakEvent) : DeEnchantmentEvent(player, false) {

    override fun setCancelled(cancel: Boolean) {
        super.setCancelled(cancel)
        event.isCancelled = cancel
    }

    override fun getDeEnchantLevel(en: DeEnchantmentWrapper): Int {
        if (deEnchantments == null) {
            deEnchantments = mutableMapOf()
            entity.equipment?.itemInMainHand?.enchantments?.forEach { (t, u) ->
                if (t !is DeEnchantmentWrapper) return@forEach
                deEnchantments!![t] = u
            }
        }
        return deEnchantments!![en] ?: 0
    }
}