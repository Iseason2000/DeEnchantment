package top.iseason.bukkit.deenchantment.events

import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import top.iseason.bukkit.deenchantment.manager.DeEnchantmentWrapper


open class DeEnchantmentEvent(val entity: LivingEntity, async: Boolean) : Event(async), Cancellable {

    protected var deEnchantments: MutableMap<DeEnchantmentWrapper, Int>? = null

    /**
     * 获取某个负魔的等级
     */
    open fun getDeEnchantLevel(en: DeEnchantmentWrapper): Int {
        if (deEnchantments == null) {
            deEnchantments = mutableMapOf()
            entity.equipment?.armorContents?.forEach {
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

    private var cancel = false
    override fun isCancelled() = cancel

    override fun setCancelled(cancel: Boolean) {
        this.cancel = cancel
    }
}

///**
// * 获取装备某种负魔等级之和
// */
//fun <T : LivingEntity> T.getArmorDeEnchant(en: DeEnchantmentWrapper): Int {
//    var count = 0
//    equipment?.armorContents?.forEach {
//        if (it == null) return@forEach
//        for ((enchantment, level) in it.enchantments) {
//            if (enchantment != en) continue
//            count += level
//        }
//    } ?: return 0
//    return count
//}
//
///**
// * 获取手上物品的某种负魔等级
// */
//fun <T : LivingEntity> T.getHandDeEnchant(en: DeEnchantmentWrapper): Int {
//    var count = 0
//    equipment?.itemInMainHand?.enchantments?.forEach { (e, l) ->
//        if (e != en) return@forEach
//        count += l
//    } ?: return 0
//    return count
//}

fun <T : Event> T.call(): T {
    Bukkit.getPluginManager().callEvent(this)
    return this
}
