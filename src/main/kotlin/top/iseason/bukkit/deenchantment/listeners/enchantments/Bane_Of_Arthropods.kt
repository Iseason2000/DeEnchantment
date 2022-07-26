package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import top.iseason.bukkit.bukkittemplate.config.annotations.Comment
import top.iseason.bukkit.bukkittemplate.config.annotations.Key
import top.iseason.bukkit.deenchantment.events.DeEntityMainHandEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkit.deenchantment.utils.EntityTools

//截肢之友
object Bane_Of_Arthropods : BaseEnchant(DeEnchantments.DE_bane_of_arthropods) {
    @Key
    @Comment("", "每级减少的伤害")
    var reduce = 2.5

    @EventHandler
    fun onDeEntityMainHandEvent(event: DeEntityMainHandEvent) {
        val entity = event.event.entity as? LivingEntity ?: return
        if (!EntityTools.isArthropods(entity)) return
        val level = event.getDeEnchantLevel(enchant)
        event.event.damage -= reduce * level
    }
}