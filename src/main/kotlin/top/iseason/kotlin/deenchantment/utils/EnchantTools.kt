package top.iseason.kotlin.deenchantment.utils

import io.github.bananapuncher714.nbteditor.NBTEditor
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.ItemMeta
import top.iseason.kotlin.deenchantment.DeEnchantmentWrapper
import top.iseason.kotlin.deenchantment.manager.ConfigManager

object EnchantTools {
    fun setDeEnchantLore(itemMeta: ItemMeta) {
        if (itemMeta is EnchantmentStorageMeta) {
            setLoreWithEnchants(itemMeta, itemMeta.storedEnchants)
            return
        }
        setLoreWithEnchants(itemMeta, itemMeta.enchants)

    }

    private fun clearEnchantLore(itemMeta: ItemMeta) {
        if (!itemMeta.hasLore()) return
        var loreList = itemMeta.lore!!
        val deEnchantmentsList = ConfigManager.getDeEnchantmentsNameList()
        for (name in deEnchantmentsList) {
            loreList = loreList.filterNot { it.matches(Regex("$name \\w+?")) }
        }
        itemMeta.lore = loreList
    }

    private fun setLoreWithEnchants(itemMeta: ItemMeta, enchants: Map<Enchantment, Int>) {
        clearEnchantLore(itemMeta)
        if (enchants.isEmpty()) return
        val loreList = itemMeta.lore ?: mutableListOf<String>()
        for ((enchant, level) in enchants) {
            if (enchant !is DeEnchantmentWrapper) continue
            val enchantmentName = ConfigManager.getEnchantmentName(enchant.name)!!
            val wholeName = "$enchantmentName ${Tools.intToRome(level)}"
            loreList.add(0, wholeName)
        }
        itemMeta.lore = loreList
    }

    fun combineEnchantments(en1: Map<Enchantment, Int>, en2: Map<Enchantment, Int>): Map<Enchantment, Int> {
        //todo: 功能未完善
        if (en1.isNullOrEmpty())
            return en2
        if (en2.isNullOrEmpty())
            return en1
        val map = mutableMapOf<Enchantment, Int>()
        for ((e1, l1) in en1) {
//            if (e1 !is DeEnchantmentWrapper) continue
            for ((e2, l2) in en2) {
//                if (e2 !is DeEnchantmentWrapper) continue
                if (e1 != e2) continue
                val level = when {
                    l1 == l2 -> l1 + 1
                    l1 > l2 -> l1
                    else -> l2
                }
                map[e1] = level
                break
            }
        }
        return map
    }

    fun getRepairCost(item: ItemStack): Int {
        return if (NBTEditor.contains(item, "RepairCost")) {
            NBTEditor.getInt(item, "RepairCost")
        } else 0
    }

    fun setRepairCost(item: ItemStack, cost: Int) :ItemStack{
        return NBTEditor.set(item, cost,  "RepairCost")
    }

}