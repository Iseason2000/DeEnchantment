package top.iseason.bukkit.deenchantment.listeners

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.event.Listener
import top.iseason.bukkit.bukkittemplate.config.SimpleYAMLConfig
import top.iseason.bukkit.bukkittemplate.config.annotations.Comment
import top.iseason.bukkit.bukkittemplate.config.annotations.Key
import top.iseason.bukkit.deenchantment.manager.DeEnchantmentWrapper
import top.iseason.bukkit.deenchantment.utils.DeEnum

abstract class BaseEnchant(
    private val enchant: DeEnchantmentWrapper
) : SimpleYAMLConfig("enchantments/${enchant.name.lowercase()}.yml"), Listener {

    @Comment("", "负魔名称")
    @Key
    var translate_name: String = enchant.translateName

    @Comment("", "负魔目标,在此挑选: https://bukkit.windit.net/javadoc/org/bukkit/enchantments/EnchantmentTarget.html")
    @Key
    var target: String = enchant.myItemTarget.name

    @Comment("", "负魔变异概率 0~1")
    @Key
    var chance: Double = enchant.chance

    @Comment("", "负魔最大等级")
    @Key
    var max_level: Int = enchant.myMaxLevel

    @Comment("", "互相冲突的负魔")
    @Key
    var conflicts: List<String> = enchant.conflicts.map { it.name }

    fun checkEnchantment(e: Enchantment) = enchant == e

    override val onLoaded: (FileConfiguration.() -> Unit) = {
        enchant.translateName = translate_name
        enchant.chance = chance
        enchant.myMaxLevel = max_level
        try {
            enchant.myItemTarget = EnchantmentTarget.valueOf(target.uppercase())
            val mutableSetOf = mutableSetOf<DeEnum>()
            for (conflict in conflicts) {
                try {
                    mutableSetOf.add(DeEnum.valueOf(conflict.uppercase()))
                } catch (e: Exception) {
                    continue
                }
            }
            enchant.conflicts = mutableSetOf
        } catch (_: Exception) {
        }
    }
}