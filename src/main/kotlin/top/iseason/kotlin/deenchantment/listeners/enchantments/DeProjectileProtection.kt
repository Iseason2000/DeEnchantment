package top.iseason.kotlin.deenchantment.listeners.enchantments

import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import top.iseason.kotlin.deenchantment.manager.DeEnchantment

//弹射物穿透
class DeProjectileProtection : Listener {
    //todo : 吸引箭？
    @EventHandler
    fun onEntityDamageEvent(event: EntityDamageEvent) {
        if (event.isCancelled) return
        if (event.cause != EntityDamageEvent.DamageCause.PROJECTILE) return
        val entity = event.entity
        if (entity !is LivingEntity) return
        val equipments = entity.equipment?.armorContents ?: return
        var levelCount = 0
        for (equipment in equipments) {
            val enchantments = equipment?.enchantments ?: continue
            for ((enchant, l) in enchantments) {
                if (enchant == DeEnchantment.DE_projectile_protection)
                    levelCount += l
            }
        }
        if (levelCount == 0) return
        event.damage = event.damage + event.damage * (levelCount * 8 * 0.01)
    }
}