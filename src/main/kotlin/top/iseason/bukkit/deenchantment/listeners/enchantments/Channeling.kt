package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Trident
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileHitEvent
import top.iseason.bukkit.bukkittemplate.config.annotations.Comment
import top.iseason.bukkit.bukkittemplate.config.annotations.Key
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments

//避雷针
object Channeling : BaseEnchant(DeEnchantments.DE_channeling) {
    @Key
    @Comment("", "是否只有效果，无伤害")
    var isEffect = false

    @Key
    @Comment("", "额外伤害等级乘数")
    var extraDamage = 0.0

    @EventHandler
    fun onProjectileHitEvent(event: ProjectileHitEvent) {
        val entity = event.entity as? Trident ?: return
        if (event.hitEntity == null) return
        if (!entity.world.isThundering) return
        val shooter = entity.shooter as? LivingEntity ?: return
        if (!checkPermission(shooter as? Player)) return
        val level = entity.item.getDeLevel()
        if (level <= 0) return
        val lightning = if (!isEffect)
            shooter.world.strikeLightning(shooter.location)
        else
            shooter.world.strikeLightningEffect(shooter.location)
        if (extraDamage > 0.0)
            shooter.damage(extraDamage * level, lightning)
    }
}