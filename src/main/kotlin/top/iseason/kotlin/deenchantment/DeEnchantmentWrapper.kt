package top.iseason.kotlin.deenchantment

import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.inventory.ItemStack
import top.iseason.kotlin.deenchantment.manager.DeEnchantment.getDeEnum

class DeEnchantmentWrapper(name: DeEnum) : Enchantment(NamespacedKey.minecraft(name.name.toLowerCase())) {
    private val myName: String = name.name
    var myItemTarget: EnchantmentTarget = EnchantmentTarget.BREAKABLE
    var myMaxLevel: Int = 1
    var myStartLevel: Int = 1
    var myIsTreasure: Boolean = false
    var myIsCursed: Boolean = false
    var conflictsWithEnchantment: Set<DeEnum> = emptySet()

    fun getEnchantment(): Enchantment {
        return getByKey(key)!!
    }

    override fun getMaxLevel(): Int {
        return myMaxLevel
    }

    override fun getStartLevel(): Int {
        return myStartLevel
    }

    override fun getItemTarget(): EnchantmentTarget {
        return myItemTarget
    }

    override fun canEnchantItem(item: ItemStack): Boolean {
        val keyName = key.key.replace("de_", "").toLowerCase()
        val source = getByKey(NamespacedKey.minecraft(keyName))//原附魔
        return source?.canEnchantItem(item)?: true
    }
    override fun getName(): String {
        return myName
    }

    override fun isTreasure(): Boolean {
        return myIsTreasure
    }

    override fun isCursed(): Boolean {
        return myIsCursed
    }
//    fun getDisplayName(level : Int): String {
//        val levelRome = Tools.intToRome(level)
//    }

    override fun conflictsWith(other: Enchantment): Boolean {
        val otherName = other.key.key
        val thisName: String = if (key.key.contains("de_"))
            key.key.replace("de_", "")
        else
            key.key
        if (otherName == thisName) return true
        return conflictsWithEnchantment.contains(getDeEnum(other))
    }

//    private fun registerEnchantment() {
//        var registered = true
//        try {
//            val field = Enchantment::class.java.getDeclaredField("acceptingNew")
//            field.isAccessible = true
//            field.set(null, true)
//            registerEnchantment(this)
//            field.set(null, false)
//        } catch (e: Exception) {
//            registered = false
//            if (e !is IllegalArgumentException) //忽略已存在的
//                e.printStackTrace()
//        }
//        if (registered)
//            LogSender.log("${ChatColor.GREEN}已添加: ${ChatColor.YELLOW} ${this.myName}")
//
//    }


}