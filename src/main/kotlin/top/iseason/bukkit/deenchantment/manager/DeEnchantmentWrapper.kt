package top.iseason.bukkit.deenchantment.manager

import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.inventory.ItemStack
import top.iseason.bukkit.deenchantment.manager.DeEnchantment.getDeEnum
import top.iseason.bukkit.deenchantment.utils.DeEnum

class DeEnchantmentWrapper(name: DeEnum) : Enchantment(NamespacedKey.minecraft(name.name.lowercase())) {
    private val myName: String = name.name
    var translateName: String = name.name
    var chance: Double = 0.2
    var myItemTarget: EnchantmentTarget = EnchantmentTarget.BREAKABLE
    var myMaxLevel: Int = 1
    var myStartLevel: Int = 1
    var myIsTreasure: Boolean = false
    var myIsCursed: Boolean = false
    var conflicts: Set<DeEnum> = emptySet()

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
        return myItemTarget.includes(item)
    }


    //    @Deprecated("Deprecated in Java")
    override fun getName(): String {
        return myName
    }

    override fun isTreasure(): Boolean {
        return myIsTreasure
    }

    override fun isCursed(): Boolean {
        return myIsCursed
    }

    override fun conflictsWith(other: Enchantment): Boolean {
        val otherName = other.key.key
        val thisName: String = if (key.key.contains("de_"))
            key.key.replace("de_", "")
        else
            key.key
        if (otherName == thisName) return true
        return conflicts.contains(getDeEnum(other))
    }
}