package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import top.iseason.bukkit.bukkittemplate.config.annotations.Comment
import top.iseason.bukkit.bukkittemplate.config.annotations.Key
import top.iseason.bukkit.deenchantment.events.DeEntityHurtEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkit.deenchantment.utils.Tools

object Blast_Protection : BaseEnchant(DeEnchantments.DE_blast_protection) {

    @Key
    @Comment("", "爆炸概率等级乘数 0~1")
    var changce = 0.05

    @Key
    @Comment("", "爆炸威力等级乘数")
    var explosionRate = 0.2

    @Key
    @Comment("", "爆炸是否有火焰")
    var allowFire = false

    @Key
    @Comment("", "爆炸是否破坏方块")
    var allowDestroy = false

    //瞬间爆炸 受到攻击概率爆炸
    @EventHandler
    fun onEntityDamageEvent(event: DeEntityHurtEvent) {
        if (event.event.cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION ||
            event.event.cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
        ) return
        val entity = event.entity
        val levelCount = event.getDeEnchantLevel(enchant)
        if (levelCount == 0) return
        if (Tools.getRandomDouble() > levelCount * changce) return
        entity.world.createExplosion(
            entity.location,
            (explosionRate * levelCount).toFloat(),
            allowFire,
            allowDestroy,
            entity
        )
    }

}