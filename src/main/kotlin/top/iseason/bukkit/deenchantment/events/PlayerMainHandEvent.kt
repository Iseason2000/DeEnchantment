package top.iseason.bukkit.deenchantment.events

import org.bukkit.entity.Player
import top.iseason.bukkit.deenchantment.manager.DeEnchantmentWrapper

class PlayerMainHandEvent(player: Player) : PlayerDeEnchantmentEvent(player) {

    private var deEnchantments: MutableMap<DeEnchantmentWrapper, Int>? = null

    override fun getDeEnchantLevel(en: DeEnchantmentWrapper): Int {
        if (deEnchantments == null) {
            deEnchantments = mutableMapOf()
            player.equipment?.itemInMainHand?.enchantments?.forEach { (t, u) ->
                if (t !is DeEnchantmentWrapper) return@forEach
                deEnchantments!![t] = u
            }
        }
        return deEnchantments!![en] ?: 0
    }
}
