package top.iseason.bukkit.deenchantment.events

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import top.iseason.bukkit.deenchantment.manager.DeEnchantmentWrapper


open class PlayerDeEnchantmentEvent(val player: Player) : Event(true) {

    private var deEnchantments: MutableMap<DeEnchantmentWrapper, Int>? = null

    /**
     * 获取某个负魔的等级
     */
    open fun getDeEnchantLevel(en: DeEnchantmentWrapper): Int {
        if (deEnchantments == null) {
            deEnchantments = mutableMapOf()
            player.equipment?.armorContents?.forEach {
                if (it == null) return@forEach
                for ((enchantment, level) in it.enchantments) {
                    if (enchantment !is DeEnchantmentWrapper) continue
                    deEnchantments!![enchantment] = (deEnchantments!![enchantment] ?: 0) + level
                }
            }
        }
        return deEnchantments!![en] ?: 0
    }

    companion object {
        @JvmStatic
        private val handlers = HandlerList()

        @JvmStatic
        fun getHandlerList() = handlers

    }

    override fun getHandlers(): HandlerList {
        return Companion.handlers
    }
}