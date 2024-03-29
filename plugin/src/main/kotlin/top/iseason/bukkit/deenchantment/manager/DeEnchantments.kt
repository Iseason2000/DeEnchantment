package top.iseason.bukkit.deenchantment.manager

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentTarget
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnum.*
import top.iseason.bukkit.deenchantment.settings.Config
import top.iseason.bukkittemplate.debug.info


object DeEnchantments {
    //保护
    val DE_protection: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_PROTECTION)
        .apply {
            translateName = "§7保护不了"
            description = "§8 - 增加受到的伤害"
            myItemTarget = EnchantmentTarget.ARMOR
            myMaxLevel = 4
            myStartLevel = 1
            conflicts = setOf(DE_BLAST_PROTECTION.name, DE_PROJECTILE_PROTECTION.name, DE_FIRE_PROTECTION.name)
        }

    //火焰保护
    val DE_fire_protection: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_FIRE_PROTECTION)
        .apply {
            translateName = "§7易燃"
            description = "§8 - 受到攻击时有概率着火"
            myItemTarget = EnchantmentTarget.ARMOR
            myMaxLevel = 4
            conflicts = setOf(DE_BLAST_PROTECTION.name, DE_PROJECTILE_PROTECTION.name, DE_PROTECTION.name)

        }

    //爆炸保护
    val DE_blast_protection: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_BLAST_PROTECTION)
        .apply {
            translateName = "§7瞬间爆炸"
            description = "§8 - 受到伤害时有概率爆炸"
            myItemTarget = EnchantmentTarget.ARMOR
            myMaxLevel = 4
            conflicts = setOf(DE_FIRE_PROTECTION.name, DE_PROJECTILE_PROTECTION.name, DE_PROTECTION.name)

        }

    //弹射物保护
    val DE_projectile_protection: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_PROJECTILE_PROTECTION)
        .apply {
            translateName = "§7弹射物吸引"
            description = "§8 - 吸引附近的弹射物"
            myItemTarget = EnchantmentTarget.ARMOR
            myMaxLevel = 4
            conflicts = setOf(DE_FIRE_PROTECTION.name, DE_BLAST_PROTECTION.name, DE_PROTECTION.name)

        }

    //摔落保护
    val DE_feather_falling: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_FEATHER_FALLING)
        .apply {
            translateName = "§7摔落骨折"
            description = "§8 - 增加受到的摔落伤害"
            myItemTarget = EnchantmentTarget.ARMOR_FEET
            myMaxLevel = 4
        }

    //水下呼吸
    val DE_respiration: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_RESPIRATION)
        .apply {
            translateName = "§7水下窒息"
            description = "§8 - 消耗氧气的速度增加"
            myItemTarget = EnchantmentTarget.ARMOR_HEAD
            myMaxLevel = 3
        }

    //水下速掘
    val DE_aqua_affinity: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_AQUA_AFFINITY)
        .apply {
            translateName = "§7水下慢掘"
            description = "§8 - 水下挖掘有概率失败"
            myItemTarget = EnchantmentTarget.ARMOR_HEAD
            myMaxLevel = 1
        }

    //荆棘
    val DE_thorns: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_THORNS)
        .apply {
            translateName = "§7负荆请罪"
            description = "§8 - 疾跑时有概率受伤"
            myItemTarget = EnchantmentTarget.ARMOR
            myMaxLevel = 3

        }

    //深海探索者
    val DE_depth_strider: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_DEPTH_STRIDER)
        .apply {
            translateName = "§7旱鸭子"
            description = "§8 - 水下视野减少"
            myItemTarget = EnchantmentTarget.ARMOR_FEET
            myMaxLevel = 3
            conflicts = setOf(DE_FROST_WALKER.name)

        }

    //冰霜行者
    val DE_frost_walker: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_FROST_WALKER)
        .apply {
            translateName = "§7熔岩行者"
            description = "§8 - 在岩浆上行走"
            myItemTarget = EnchantmentTarget.ARMOR_FEET
            myMaxLevel = 2
            conflicts = setOf(DE_DEPTH_STRIDER.name)

        }

    //绑定诅咒
    val DE_binding_curse: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_BINDING_CURSE)
        .apply {
            translateName = "§a灵魂绑定"
            description = "§8 - 绑定玩家的灵魂仅允许使用"
            myItemTarget = EnchantmentTarget.BREAKABLE
            myMaxLevel = 1
            conflicts = setOf(DE_VANISHING_CURSE.name)
            myIsTreasure = true
        }

    //锋利
    val DE_sharpness: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_SHARPNESS)
        .apply {
            translateName = "§7磨钝"
            description = "§8 - 攻击伤害减少"
            myItemTarget = EnchantmentTarget.WEAPON
            myMaxLevel = 5
            conflicts = setOf(DE_SMITE.name, DE_BANE_OF_ARTHROPODS.name)
        }

    //亡灵杀手
    val DE_smite: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_SMITE)
        .apply {
            translateName = "§7亡灵之友"
            description = "§8 - 对亡灵生物伤害减少"
            myItemTarget = EnchantmentTarget.WEAPON
            myMaxLevel = 5
            conflicts = setOf(DE_SHARPNESS.name, DE_BANE_OF_ARTHROPODS.name)
        }

    //截肢杀手
    val DE_bane_of_arthropods: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_BANE_OF_ARTHROPODS)
        .apply {
            translateName = "§7截肢之友"
            description = "§8 - 对截肢生物伤害减少"
            myItemTarget = EnchantmentTarget.WEAPON
            myMaxLevel = 5
            conflicts = setOf(DE_SHARPNESS.name, DE_SMITE.name)
        }

    //击退
    val DE_knockback: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_KNOCKBACK)
        .apply {
            translateName = "§7退击"
            description = "§8 - 攻击时后跳一段距离"
            myItemTarget = EnchantmentTarget.WEAPON
            myMaxLevel = 2
        }

    //火焰附加
    val DE_fire_aspect: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_FIRE_ASPECT)
        .apply {
            translateName = "§7引火烧身"
            description = "§8 - 攻击时有概率烧伤自己"
            myItemTarget = EnchantmentTarget.WEAPON
            myMaxLevel = 2
        }

    //抢夺
    val DE_looting: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_LOOTING)
        .apply {
            translateName = "§7知足"
            description = "§8 - 击杀生物有概率没有掉落物"
            myItemTarget = EnchantmentTarget.WEAPON
            myMaxLevel = 3
        }

    //横扫
    val DE_sweeping: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_SWEEPING)
        .apply {
            translateName = "§7牵引之刃"
            description = "§8 - 将范围内的敌人吸引过来"
            myItemTarget = EnchantmentTarget.WEAPON
            myMaxLevel = 3
        }

    //效率
    val DE_efficiency: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_EFFICIENCY)
        .apply {
            translateName = "§7低效"
            description = "§8 - 有概率挖掘失败"
            myItemTarget = EnchantmentTarget.TOOL
            myMaxLevel = 5
        }

    //精准采集
    val DE_silk_touch: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_SILK_TOUCH)
        .apply {
            translateName = "§7彻底粉碎"
            description = "§8 - 挖掘方块不会有掉落物"
            myItemTarget = EnchantmentTarget.TOOL
            myMaxLevel = 1
            conflicts = setOf(DE_FORTUNE.name)
        }

    // 耐久
    val DE_unbreaking: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_UNBREAKING)
        .apply {
            translateName = "§7易损"
            description = "§8 - 增加耐久消耗"
            myItemTarget = EnchantmentTarget.BREAKABLE
            myMaxLevel = 3
        }

    // 时运
    val DE_fortune: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_FORTUNE)
        .apply {
            translateName = "§7霉运"
            description = "§8 - 挖掘方块掉落物有概率消失"
            myItemTarget = EnchantmentTarget.TOOL
            myMaxLevel = 3
            conflicts = setOf(DE_SILK_TOUCH.name)
        }

    //力量
    val DE_power: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_POWER)
        .apply {
            translateName = "§7虚弱"
            description = "§8 - 箭矢伤害和速度减少"
            myItemTarget = EnchantmentTarget.BOW
            myMaxLevel = 5
        }

    // 冲击
    val DE_punch: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_PUNCH)
        .apply {
            translateName = "§7拉扯"
            description = "§8 - 将目标拉扯过来"
            myItemTarget = EnchantmentTarget.BOW
            myMaxLevel = 2
        }

    // 火矢
    val DE_flame: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_FLAME)
        .apply {
            translateName = "§7神速"
            description = "§8 - 箭矢速度增加"
            myItemTarget = EnchantmentTarget.BOW
            myMaxLevel = 1
        }

    // 无限
    val DE_infinity: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_INFINITY)
        .apply {
            translateName = "§7多重"
            description = "§8 - 消耗一根箭射出多支箭"
            myItemTarget = EnchantmentTarget.BOW
            myMaxLevel = 2
            conflicts = setOf(DE_MENDING.name)
        }

    //海之眷顾
    val DE_luck_of_the_sea: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_LUCK_OF_THE_SEA)
        .apply {
            translateName = "§7海之嫌弃"
            description = "§8 - 钓到的东西有概率脱钩"
            myItemTarget = EnchantmentTarget.FISHING_ROD
            myMaxLevel = 3
        }

    // 钓饵
    val DE_lure: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_LURE)
        .apply {
            translateName = "§7过期钓饵"
            description = "§8 - 增加鱼上钩的时间"
            myItemTarget = EnchantmentTarget.FISHING_ROD
            myMaxLevel = 3
        }

    // 忠诚
    val DE_loyalty: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_LOYALTY)
        .apply {
            translateName = "§7背叛"
            description = "§8 - 三叉戟有概率叛逃他人"
            myItemTarget = EnchantmentTarget.TRIDENT
            myMaxLevel = 3
            conflicts = setOf(DE_RIPTIDE.name)
        }

    // 穿刺
    val DE_impaling: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_IMPALING)
        .apply {
            translateName = "§7刺穿"
            description = "§8 - 增加对非水生生物的伤害"
            myItemTarget = EnchantmentTarget.TRIDENT
            myMaxLevel = 5
        }

    // 激流
    val DE_riptide: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_RIPTIDE)
        .apply {
            translateName = "§7焰流"
            description = "§8 - 在燃烧时快速移动"
            myItemTarget = EnchantmentTarget.TRIDENT
            myMaxLevel = 3
            conflicts = setOf(DE_LOYALTY.name, DE_CHANNELING.name)
        }

    // 引雷
    val DE_channeling: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_CHANNELING)
        .apply {
            translateName = "§7引雷针"
            description = "§8 - 在雷雨天时会遭天谴"
            myItemTarget = EnchantmentTarget.TRIDENT
            myMaxLevel = 1
            conflicts = setOf(DE_RIPTIDE.name)
        }

    // 多重射击
    val DE_multishot: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_MULTISHOT)
        .apply {
            translateName = "§7连珠"
            description = "§8 - 连续射出多支箭"
            myItemTarget = EnchantmentTarget.CROSSBOW
            myMaxLevel = 1
            conflicts = setOf(DE_PIERCING.name)
        }

    // 快速装填
    val DE_quick_charge: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_QUICK_CHARGE)
        .apply {
            translateName = "§7慢速装填"
            description = "§8 - 填充速度减慢"
            myItemTarget = EnchantmentTarget.CROSSBOW
            myMaxLevel = 3
            conflicts = setOf(DE_PIERCING.name)
        }

    //穿透
    val DE_piercing: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_PIERCING)
        .apply {
            translateName = "§7反弹"
            description = "§8 - 击中生物时反弹"
            myItemTarget = EnchantmentTarget.CROSSBOW
            myMaxLevel = 4
            conflicts = setOf(DE_MULTISHOT.name)
        }

    // 经验修补
    val DE_mending: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_MENDING)
        .apply {
            translateName = "§7经验反哺"
            description = "§8 - 消耗耐久增加经验"
            myItemTarget = EnchantmentTarget.BREAKABLE
            myMaxLevel = 1
        }

    //消失诅咒
    val DE_vanishing_curse: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_VANISHING_CURSE)
        .apply {
            translateName = "§a永存祝福"
            description = "§8 - 物品将永远陪伴你"
            myItemTarget = EnchantmentTarget.BREAKABLE
            myMaxLevel = 1
            conflicts = setOf(DE_BINDING_CURSE.name)
            myIsTreasure = true
        }

    // 灵魂疾行
    val DE_soul_speed: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_SOUL_SPEED)
        .apply {
            translateName = "§7大地疾行"
            description = "§8 - 在土地上疾行"
            myItemTarget = EnchantmentTarget.ARMOR_FEET
            myMaxLevel = 3
        }

    // 迅捷潜行
    val DE_swift_sneak: DeEnchantmentWrapper = DeEnchantmentWrapper(DE_SWIFT_SNEAK)
        .apply {
            translateName = "§7沉重步伐"
            description = "§8 - 缓慢前行但抗性提升"
            myItemTarget = EnchantmentTarget.ARMOR_LEGS
            myMaxLevel = 3
            myIsTreasure = true
        }

    /**
     * 注册负魔
     */
    fun registerEnchantments() {

        val acceptingNew = Enchantment::class.java.getDeclaredField("acceptingNew")
        acceptingNew.isAccessible = true
        acceptingNew.set(null, true)
        var count = 0
        val enchants = BaseEnchant.enchantConfigs
        val totalCount = enchants.filter { it.enable }.size
        enchants.forEach { enc ->
            val deEnchant = enc.enchant
            if (!deEnchant.enable) return@forEach
            Bukkit.getPluginManager().addPermission(enc.permission)
            Enchantment.registerEnchantment(deEnchant)
            count++
            if (!Config.cleanConsole) {
                info(
                    "${ChatColor.GREEN}已添加${ChatColor.GRAY}" +
                            "(${ChatColor.GOLD}${count}${ChatColor.GREEN}/${ChatColor.AQUA}$totalCount" +
                            "${ChatColor.GRAY}):${deEnchant.translateName}"
                )
            }
        }
        if (count == 0 || count != totalCount) {
            info(
                "${ChatColor.YELLOW}负魔注册异常!"
                        + "(${ChatColor.GOLD}${count}${ChatColor.GREEN}/${ChatColor.AQUA}$totalCount)"
            )
            info(
                "${ChatColor.YELLOW}请尝试输入 ${ChatColor.GREEN}de reload ${ChatColor.YELLOW}以重新注册负魔!"
            )
        } else {
            info(
                "${ChatColor.GREEN}负魔注册完毕" + "(${ChatColor.GOLD}${count}${ChatColor.GREEN}/${ChatColor.AQUA}$totalCount)"
            )
        }
        stopRegisterEnchantment()
    }

    //注销负魔
    fun resetEnchantments() {
        val keyField = Enchantment::class.java.getDeclaredField("byKey")
        val nameField = Enchantment::class.java.getDeclaredField("byName")
        keyField.isAccessible = true
        nameField.isAccessible = true
        val keyMap = keyField[null] as HashMap<*, *>
        val nameMap = nameField[null] as HashMap<*, *>
        val totalCount = BaseEnchant.enchants.filter { it.enable }.size
        var count = 1
        BaseEnchant.enchantConfigs.forEach { en ->
            val enchant = en.enchant
            Bukkit.getPluginManager().removePermission(en.permission)
            if (!enchant.enable) return@forEach
            keyMap.remove(enchant.key)
            nameMap.remove(enchant.name)
            if (!Config.cleanConsole) {
                info(
                    "${ChatColor.YELLOW}已注销${ChatColor.GRAY}" +
                            "(${ChatColor.GOLD}${count++}${ChatColor.GREEN}/${ChatColor.AQUA}$totalCount" +
                            "${ChatColor.GRAY}):${enchant.translateName}"
                )
            }
        }
        keyField.isAccessible = false
        nameField.isAccessible = false
        info("${ChatColor.YELLOW}负魔注销完毕!")

    }


    private fun stopRegisterEnchantment() {
        val field = Enchantment::class.java.getDeclaredField("acceptingNew")
        field.isAccessible = true
        field.set(null, false)
        field.isAccessible = false
    }

    fun getDeEnum(enchantment: Enchantment): DeEnum? {
        val name = enchantment.key.key
        return try {
            DeEnum.valueOf("de_$name".uppercase())
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    fun getByKeyName(keyName: String): Enchantment? {
        return Enchantment.getByKey(NamespacedKey.minecraft(keyName))
    }

    fun getByDeEnum(deEnum: DeEnum): Enchantment? {
        return getByKeyName(deEnum.toString().lowercase())
    }
}