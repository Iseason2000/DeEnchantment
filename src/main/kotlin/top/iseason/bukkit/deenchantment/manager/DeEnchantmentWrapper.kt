package top.iseason.bukkit.deenchantment.manager

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.inventory.ItemStack
import top.iseason.bukkit.deenchantment.manager.DeEnchantments.getDeEnum

class DeEnchantmentWrapper(val enum: DeEnum) : Enchantment(NamespacedKey.minecraft(enum.name.lowercase())) {
    var enable = true
    private val myName: String = enum.name
    var translateName: String = enum.name
    var description: String = ""
    var chance: Double = 0.2
    var myItemTarget: EnchantmentTarget = EnchantmentTarget.BREAKABLE
    var myMaxLevel: Int = 1
    var myStartLevel: Int = 1
    var myIsTreasure: Boolean = false
    var myIsCursed: Boolean = false
    var conflicts: Set<String> = emptySet()

    fun getEnchantment(): Enchantment {
        return getByKey(NamespacedKey.minecraft(key.key.removePrefix("de_")))!!
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
        if (item.type == Material.ENCHANTED_BOOK) return true
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
        val deEnum = getDeEnum(other)
        if (enum == deEnum) return true
        return conflicts.contains(other.key.key)
    }
}