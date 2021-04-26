package top.iseason.kotlin.deenchantment.manager

import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.inventory.ItemStack
import top.iseason.kotlin.deenchantment.manager.DeEnchantment.getDeEnum
import top.iseason.kotlin.deenchantment.utils.DeEnum

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

    override fun conflictsWith(other: Enchantment): Boolean {
        val otherName = other.key.key
        val thisName: String = if (key.key.contains("de_"))
            key.key.replace("de_", "")
        else
            key.key
        if (otherName == thisName) return true
        return conflictsWithEnchantment.contains(getDeEnum(other))
    }
}