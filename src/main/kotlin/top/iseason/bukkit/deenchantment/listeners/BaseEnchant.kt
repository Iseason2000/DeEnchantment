package top.iseason.bukkit.deenchantment.listeners

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault
import top.iseason.bukkit.bukkittemplate.config.SimpleYAMLConfig
import top.iseason.bukkit.bukkittemplate.config.annotations.Comment
import top.iseason.bukkit.bukkittemplate.config.annotations.Key
import top.iseason.bukkit.bukkittemplate.utils.toColor
import top.iseason.bukkit.deenchantment.events.DeEnchantmentEvent
import top.iseason.bukkit.deenchantment.hooks.EcoEnchantHook
import top.iseason.bukkit.deenchantment.manager.DeEnchantmentWrapper
import top.iseason.bukkit.deenchantment.settings.Config

abstract class BaseEnchant(
    val enchant: DeEnchantmentWrapper
) : SimpleYAMLConfig("enchantments/${enchant.name.lowercase()}.yml"), Listener {

    val permission = Permission("deenchantment.enchants.${enchant.name.lowercase()}", PermissionDefault.TRUE)

    @Comment("", "是否启用")
    @Key
    var enable: Boolean = true

    @Comment("", "负魔名称")
    @Key
    var translate_name: String = enchant.translateName

    @Comment("", "负魔描述")
    @Key
    var description: String = enchant.description

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
    var conflicts: List<String> = enchant.conflicts.toList()

    fun checkEnchantment(e: Enchantment) = enchant == e

    fun checkPermission(player: Player?): Boolean {
        if (!Config.enchantsPermission) return true
        return player?.hasPermission(permission) ?: true
    }

    override val onLoaded: (ConfigurationSection.() -> Unit) = {
        enchant.enable = enable
        enchant.translateName = translate_name.toColor()
        enchant.description = description.toColor()
        enchant.chance = chance
        enchant.myMaxLevel = max_level
        try {
            enchant.myItemTarget = EnchantmentTarget.valueOf(target.uppercase())
            val list = conflicts.map { it.lowercase() }
            conflicts = list
            enchant.conflicts = list.toHashSet()
        } catch (_: Exception) {
        }
        EcoEnchantHook.setInfo(this@BaseEnchant)
    }

    companion object {
        val enchants = mutableSetOf<DeEnchantmentWrapper>()
        val enchantConfigs = mutableSetOf<BaseEnchant>()
    }

    init {
        enchants.add(enchant)
        enchantConfigs.add(this)
    }

    /**
     * 获取装备某种负魔等级之和
     */
    fun <T : LivingEntity> T.getArmorDeEnchant(): Int {
        var count = 0
        equipment?.armorContents?.forEach {
            if (it == null) return@forEach
            for ((enchantment, level) in it.enchantments) {
                if (enchantment != enchant) continue
                count += level
            }
        } ?: return 0
        return count
    }

    /**
     * 获取手上物品的某种负魔等级
     */
    fun <T : LivingEntity> T.getHandDeEnchant(): Int {
        var count = 0
        equipment?.itemInMainHand?.enchantments?.forEach { (e, l) ->
            if (e != enchant) return@forEach
            count += l
        } ?: return 0
        return count
    }

    fun ItemStack.getDeLevel() = enchantments[enchant] ?: 0
    fun DeEnchantmentEvent.getDeLevel() = getDeEnchantLevel(enchant)

}