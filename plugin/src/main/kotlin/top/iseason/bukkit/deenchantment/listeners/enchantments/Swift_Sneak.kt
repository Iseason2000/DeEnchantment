package top.iseason.bukkit.deenchantment.listeners.enchantments

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.potion.PotionEffectType
import top.iseason.bukkit.deenchantment.events.DePlayerEquipmentChangeEvent
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkit.deenchantment.runnables.PotionAdder
import top.iseason.bukkittemplate.BukkitTemplate
import top.iseason.bukkittemplate.config.annotations.Comment
import top.iseason.bukkittemplate.config.annotations.Key
import top.iseason.bukkittemplate.utils.other.submit
import kotlin.math.min

object Swift_Sneak : BaseEnchant(DeEnchantments.DE_swift_sneak) {
    @Key
    @Comment("", "缓慢药水等级乘数")
    var slowLevelRate = 1

    @Key
    @Comment("", "最大缓慢药水等级, -1 为不限制; 0为不添加药水效果")
    var maxSlowLevel = -1

    @Key
    @Comment("", "抗性药水等级乘数")
    var resistanceLevelRate = 1

    @Key
    @Comment("", "最大抗性药水等级, -1 为不限制; 0为不添加药水效果")
    var maxResistanceLevel = 3


    private val slows = mutableMapOf<Player, PotionAdder>()
    private val resistances = mutableMapOf<Player, PotionAdder>()

    @EventHandler(ignoreCancelled = true)
    fun onEquipmentChange(event: DePlayerEquipmentChangeEvent) {
        val deLevel = event.getDeLevel()
        val player = event.player
        val potion1 = slows[player]
        val potion2 = resistances[player]
        val noEnchant = deLevel == 0 || !checkPermission(event.player)
        //取消滞留负魔
        if (potion1 != null && (noEnchant || maxSlowLevel == 0)) {
            submit {
                potion1.cancel()
            }
            slows.remove(player)
        }
        if (potion2 != null && (noEnchant || maxResistanceLevel == 0)) {
            submit {
                potion2.cancel()
            }
            resistances.remove(player)
        }
        if (noEnchant) return
        if (maxSlowLevel != 0) {
            val level = if (maxSlowLevel > 0) min(deLevel * slowLevelRate, maxSlowLevel) else deLevel * slowLevelRate
            slows.computeIfAbsent(player) {
                PotionAdder(player, PotionEffectType.SLOW, 220, level).also {
                    it.runTaskTimer(BukkitTemplate.getPlugin(), 0, 200)
                }
            }.level = level
        }
        if (maxResistanceLevel != 0) {
            val level = if (maxResistanceLevel > 0) min(
                deLevel * resistanceLevelRate,
                maxResistanceLevel
            ) else deLevel * resistanceLevelRate
            resistances.computeIfAbsent(player) {
                PotionAdder(player, PotionEffectType.DAMAGE_RESISTANCE, 220, level).also {
                    it.runTaskTimer(BukkitTemplate.getPlugin(), 0, 200)
                }
            }.level = level
        }
    }
}