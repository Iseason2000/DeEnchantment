package top.iseason.kotlin.deenchantment.manager

import org.bukkit.ChatColor
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentTarget
import top.iseason.kotlin.deenchantment.DeEnchantmentWrapper
import top.iseason.kotlin.deenchantment.DeEnum
import top.iseason.kotlin.deenchantment.DeEnum.*
import top.iseason.kotlin.deenchantment.manager.ConfigManager.getDeEnchantments
import top.iseason.kotlin.deenchantment.utils.LogSender


object DeEnchantment {
    //保护
    val DE_protection: Enchantment = DeEnchantmentWrapper(DE_PROTECTION)
        .apply {
            myItemTarget = EnchantmentTarget.ARMOR
            myMaxLevel = 4
            myStartLevel = 1
            myIsTreasure = false
            myIsCursed = false
            conflictsWithEnchantment = setOf(DE_BLAST_PROTECTION, DE_PROJECTILE_PROTECTION, DE_FIRE_PROTECTION)
        }

    //火焰保护
    val DE_fire_protection: Enchantment = DeEnchantmentWrapper(DE_FIRE_PROTECTION)
        .apply {
            myItemTarget = EnchantmentTarget.ARMOR
            myMaxLevel = 4
            conflictsWithEnchantment = setOf(DE_BLAST_PROTECTION, DE_PROJECTILE_PROTECTION, DE_PROTECTION)

        }

    //爆炸保护
    val DE_blast_protection: Enchantment = DeEnchantmentWrapper(DE_BLAST_PROTECTION)
        .apply {
            myItemTarget = EnchantmentTarget.ARMOR
            myMaxLevel = 4
            conflictsWithEnchantment = setOf(DE_FIRE_PROTECTION, DE_PROJECTILE_PROTECTION, DE_PROTECTION)

        }

    //弹射物保护
    val DE_projectile_protection: Enchantment = DeEnchantmentWrapper(DE_PROJECTILE_PROTECTION)
        .apply {
            myItemTarget = EnchantmentTarget.ARMOR
            myMaxLevel = 4
            conflictsWithEnchantment = setOf(DE_FIRE_PROTECTION, DE_BLAST_PROTECTION, DE_PROTECTION)

        }

    //摔落保护
    val DE_feather_falling: Enchantment = DeEnchantmentWrapper(DE_FEATHER_FALLING)
        .apply {
            myItemTarget = EnchantmentTarget.ARMOR_FEET
            myMaxLevel = 4
        }

    //水下呼吸
    val DE_respiration: Enchantment = DeEnchantmentWrapper(DE_RESPIRATION)
        .apply {
            myItemTarget = EnchantmentTarget.ARMOR_HEAD
            myMaxLevel = 3
        }

    //水下速掘
    val DE_aqua_affinity: Enchantment = DeEnchantmentWrapper(DE_AQUA_AFFINITY)
        .apply {
            myItemTarget = EnchantmentTarget.ARMOR_HEAD
            myMaxLevel = 1
        }

    //荆棘
    val DE_thorns: Enchantment = DeEnchantmentWrapper(DE_THORNS)
        .apply {
            myItemTarget = EnchantmentTarget.ARMOR
            myMaxLevel = 3

        }

    //深海探索者
    val DE_depth_strider: Enchantment = DeEnchantmentWrapper(DE_DEPTH_STRIDER)
        .apply {
            myItemTarget = EnchantmentTarget.ARMOR_FEET
            myMaxLevel = 3
            conflictsWithEnchantment = setOf(DE_FROST_WALKER)

        }

    //冰霜行者
    val DE_frost_walker: Enchantment = DeEnchantmentWrapper(DE_FROST_WALKER)
        .apply {
            myItemTarget = EnchantmentTarget.ARMOR_FEET
            myMaxLevel = 2
            conflictsWithEnchantment = setOf(DE_DEPTH_STRIDER)

        }

    //绑定诅咒
    val DE_binding_curse: Enchantment = DeEnchantmentWrapper(DE_BINDING_CURSE)
        .apply {
            myItemTarget = EnchantmentTarget.BREAKABLE
            myMaxLevel = 1
            conflictsWithEnchantment = setOf(DE_VANISHING_CURSE)
        }

    //锋利
    val DE_sharpness: Enchantment = DeEnchantmentWrapper(DE_SHARPNESS)
        .apply {
            myItemTarget = EnchantmentTarget.WEAPON
            myMaxLevel = 5
            conflictsWithEnchantment = setOf(DE_SMITE, DE_BANE_OF_ARTHROPODS)
        }

    //亡灵杀手
    val DE_smite: Enchantment = DeEnchantmentWrapper(DE_SMITE)
        .apply {
            myItemTarget = EnchantmentTarget.WEAPON
            myMaxLevel = 5
            conflictsWithEnchantment = setOf(DE_SHARPNESS, DE_BANE_OF_ARTHROPODS)
        }

    //截肢杀手
    val DE_bane_of_arthropods: Enchantment = DeEnchantmentWrapper(DE_BANE_OF_ARTHROPODS)
        .apply {
            myItemTarget = EnchantmentTarget.WEAPON
            myMaxLevel = 5
            conflictsWithEnchantment = setOf(DE_SHARPNESS, DE_SMITE)
        }

    //击退
    val DE_knockback: Enchantment = DeEnchantmentWrapper(DE_KNOCKBACK)
        .apply {
            myItemTarget = EnchantmentTarget.WEAPON
            myMaxLevel = 2
        }

    //火焰附加
    val DE_fire_aspect: Enchantment = DeEnchantmentWrapper(DE_FIRE_ASPECT)
        .apply {
            myItemTarget = EnchantmentTarget.WEAPON
            myMaxLevel = 2
        }

    //抢夺
    val DE_looting: Enchantment = DeEnchantmentWrapper(DE_LOOTING)
        .apply {
            myItemTarget = EnchantmentTarget.WEAPON
            myMaxLevel = 3
        }

    //横扫
    val DE_sweeping: Enchantment = DeEnchantmentWrapper(DE_SWEEPING)
        .apply {
            myItemTarget = EnchantmentTarget.WEAPON
            myMaxLevel = 3
        }

    //效率
    val DE_efficiency: Enchantment = DeEnchantmentWrapper(DE_EFFICIENCY)
        .apply {
            myItemTarget = EnchantmentTarget.TOOL
            myMaxLevel = 5
        }

    //精准采集
    val DE_silk_touch: Enchantment = DeEnchantmentWrapper(DE_SILK_TOUCH)
        .apply {
            myItemTarget = EnchantmentTarget.TOOL
            myMaxLevel = 1
            conflictsWithEnchantment = setOf(DE_FORTUNE)
        }

    // 耐久
    val DE_unbreaking: Enchantment = DeEnchantmentWrapper(DE_UNBREAKING)
        .apply {
            myItemTarget = EnchantmentTarget.BREAKABLE
            myMaxLevel = 3
        }

    // 时运
    val DE_fortune: Enchantment = DeEnchantmentWrapper(DE_FORTUNE)
        .apply {
            myItemTarget = EnchantmentTarget.TOOL
            myMaxLevel = 3
            conflictsWithEnchantment = setOf(DE_SILK_TOUCH)
        }

    //力量
    val DE_power: Enchantment = DeEnchantmentWrapper(DE_POWER)
        .apply {
            myItemTarget = EnchantmentTarget.BOW
            myMaxLevel = 5
        }

    // 冲击
    val DE_punch: Enchantment = DeEnchantmentWrapper(DE_PUNCH)
        .apply {
            myItemTarget = EnchantmentTarget.BOW
            myMaxLevel = 2
        }

    // 火矢
    val DE_flame: Enchantment = DeEnchantmentWrapper(DE_FLAME)
        .apply {
            myItemTarget = EnchantmentTarget.BOW
            myMaxLevel = 1
        }

    // 无限
    val DE_infinity: Enchantment = DeEnchantmentWrapper(DE_INFINITY)
        .apply {
            myItemTarget = EnchantmentTarget.BOW
            myMaxLevel = 3
            conflictsWithEnchantment = setOf(DE_MENDING)
        }

    //海之眷顾
    val DE_luck_of_the_sea: Enchantment = DeEnchantmentWrapper(DE_LUCK_OF_THE_SEA)
        .apply {
            myItemTarget = EnchantmentTarget.FISHING_ROD
            myMaxLevel = 3
        }

    // 钓饵
    val DE_lure: Enchantment = DeEnchantmentWrapper(DE_LURE)
        .apply {
            myItemTarget = EnchantmentTarget.FISHING_ROD
            myMaxLevel = 3
        }

    // 忠诚
    val DE_loyalty: Enchantment = DeEnchantmentWrapper(DE_LOYALTY)
        .apply {
            myItemTarget = EnchantmentTarget.TRIDENT
            myMaxLevel = 1
            conflictsWithEnchantment = setOf(DE_RIPTIDE)
        }

    // 穿刺
    val DE_impaling: Enchantment = DeEnchantmentWrapper(DE_IMPALING)
        .apply {
            myItemTarget = EnchantmentTarget.TRIDENT
            myMaxLevel = 5
        }

    // 激流
    val DE_riptide: Enchantment = DeEnchantmentWrapper(DE_RIPTIDE)
        .apply {
            myItemTarget = EnchantmentTarget.TRIDENT
            myMaxLevel = 3
            conflictsWithEnchantment = setOf(DE_LOYALTY, DE_CHANNELING)
        }

    // 引雷
    val DE_channeling: Enchantment = DeEnchantmentWrapper(DE_CHANNELING)
        .apply {
            myItemTarget = EnchantmentTarget.TRIDENT
            myMaxLevel = 1
            conflictsWithEnchantment = setOf(DE_RIPTIDE)
        }

    // 多重射击
    val DE_multishot: Enchantment = DeEnchantmentWrapper(DE_MULTISHOT)
        .apply {
            myItemTarget = EnchantmentTarget.CROSSBOW
            myMaxLevel = 1
            conflictsWithEnchantment = setOf(DE_PIERCING)
        }

    // 快速装填
    val DE_quick_charge: Enchantment = DeEnchantmentWrapper(DE_QUICK_CHARGE)
        .apply {
            myItemTarget = EnchantmentTarget.CROSSBOW
            myMaxLevel = 3
            conflictsWithEnchantment = setOf(DE_PIERCING)
        }

    //穿透
    val DE_piercing: Enchantment = DeEnchantmentWrapper(DE_PIERCING)
        .apply {
            myItemTarget = EnchantmentTarget.CROSSBOW
            myMaxLevel = 4
            conflictsWithEnchantment = setOf(DE_MULTISHOT)
        }

    // 经验修补
    val DE_mending: Enchantment = DeEnchantmentWrapper(DE_MENDING)
        .apply {
            myItemTarget = EnchantmentTarget.BREAKABLE
            myMaxLevel = 1
        }

    //消失诅咒
    val DE_vanishing_curse: Enchantment = DeEnchantmentWrapper(DE_VANISHING_CURSE)
        .apply {
            myItemTarget = EnchantmentTarget.BREAKABLE
            myMaxLevel = 1
            conflictsWithEnchantment = setOf(DE_BINDING_CURSE)
        }

    // 灵魂疾行
    val DE_soul_speed: Enchantment = DeEnchantmentWrapper(DE_SOUL_SPEED)
        .apply {
            myItemTarget = EnchantmentTarget.ARMOR_FEET
            myMaxLevel = 3
        }

    fun registerEnchantments(): Boolean {
        try {
            val deEnchantments = getDeEnchantments()
            val acceptingNew = Enchantment::class.java.getDeclaredField("acceptingNew")
            val byKey = Enchantment::class.java.getDeclaredField("byKey")
            val byName = Enchantment::class.java.getDeclaredField("byName")
            acceptingNew.isAccessible = true
            acceptingNew.set(null, true)
            byKey.isAccessible = true
            byName.isAccessible = true
            val keyMap = byKey.get(null)
            val nameMap = byName.get(null)
            ConfigManager.byKey = keyMap
            ConfigManager.byName = nameMap
            var count = 1
            val totalCount = deEnchantments.size
            for (field in javaClass.declaredFields) {
                val enchantment = field.get(this)
                if (enchantment is DeEnchantmentWrapper && deEnchantments.contains(enchantment.name)) {
                    Enchantment.registerEnchantment(enchantment)
                    ConfigManager.deEnchantmentsList.add(enchantment)
                    val enchantmentName = ConfigManager.getEnchantmentName(enchantment.name)
                    LogSender.log(
                        "${ChatColor.GREEN}已添加${ChatColor.GRAY}" +
                                "(${ChatColor.GOLD}${count++}${ChatColor.GREEN}/${ChatColor.AQUA}$totalCount" +
                                "${ChatColor.GRAY}):$enchantmentName"
                    )
                }
            }
            stopRegisterEnchantment()
            return true
        } catch (e: Exception) {
            stopRegisterEnchantment()
//            e.printStackTrace()
            LogSender.log("${ChatColor.RED}添加附魔异常,该附魔已存在，请重启服务器!")
            return false
        }

    }

    private fun stopRegisterEnchantment() {
        val field = Enchantment::class.java.getDeclaredField("acceptingNew")
        field.isAccessible = true
        field.set(null, false)
        field.isAccessible = false

    }

    fun getDeEnum(enchantment: Enchantment): DeEnum {
        val name = enchantment.key.key
        return try {
            DeEnum.valueOf(name.toUpperCase())
        } catch (e: IllegalArgumentException) {
            DeEnum.valueOf("de_$name".toUpperCase())
        }
    }
    fun getByKeyName(keyName: String) :Enchantment?{
        return  Enchantment.getByKey(NamespacedKey.minecraft(keyName))
    }
    fun getByDeEnum(deEnum : DeEnum):Enchantment?{
        return getByKeyName(deEnum.toString().toLowerCase())
    }
}