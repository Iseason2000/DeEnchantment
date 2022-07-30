package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import top.iseason.bukkit.bukkittemplate.config.annotations.Comment
import top.iseason.bukkit.bukkittemplate.config.annotations.Key
import top.iseason.bukkit.bukkittemplate.utils.submit
import top.iseason.bukkit.deenchantment.events.DePlayerEquipmentChangeEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkit.deenchantment.runnables.ProjectileCollector
import top.iseason.bukkit.deenchantment.runnables.TargetFinder
import kotlin.math.min

//弹射物吸引
object Projectile_Protection : BaseEnchant(DeEnchantments.DE_projectile_protection) {
    @Key
    @Comment("", "受否仅允许玩家有效，为 false 时对所有怪物有效，计算消耗大幅增大")
    var playerOnly = true

    @Key
    @Comment("", "搜索半径等级系数, 当playerOnly 为 true 时有效")
    var radius = 1.0

    @Key
    @Comment("", "最大搜索半径, 当playerOnly 为 true 时有效")
    var maxRadius = 10.0

    @Key
    @Comment("", "搜索时间间隔, 单位tick")
    var period: Long = 5

    var collectors = mutableMapOf<Player, ProjectileCollector>()

    @EventHandler(ignoreCancelled = true)
    fun onPlayerEquipmentChange(event: DePlayerEquipmentChangeEvent) {
        val player = event.player
        collectors[player]?.cancel()
        if (!playerOnly) return
        val deLevel = event.getDeLevel()
        if (deLevel == 0) return
        val radius = min(radius * deLevel, maxRadius)
        val projectileCollector = ProjectileCollector(event.player, radius)
        collectors[player] = projectileCollector
        submit(0, period, task = projectileCollector)
    }

    @EventHandler(ignoreCancelled = true)
    fun onProjectileLaunchEvent(event: ProjectileLaunchEvent) {
        if (playerOnly) return
        //超过128个任务则不会创建，防止卡顿
        if (!TargetFinder.isMaxPool()) {
            submit(period, period, task = TargetFinder(event.entity))
        }
    }

    @EventHandler
    fun onProjectileHitEvent(event: ProjectileHitEvent) {
        if (playerOnly) return
        val entity = event.entity
        TargetFinder.remove(entity)
        entity.setGravity(true)
    }

    //增加伤害
//    @EventHandler
//    fun onEntityDamageEvent(event: EntityDamageEvent) {
//        if (event.isCancelled) return
//        if (event.cause != EntityDamageEvent.DamageCause.PROJECTILE) return
//        val entity = event.entity
//        if (entity !is LivingEntity) return
//        val levelCount = EnchantTools.getLevelCount(entity, DeEnchantments.DE_projectile_protection)
//        if (levelCount == 0) return
//        event.damage = event.damage + event.damage * (levelCount * 8 * 0.01)
//    }

}