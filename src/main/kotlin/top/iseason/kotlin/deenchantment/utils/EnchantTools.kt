package top.iseason.kotlin.deenchantment.utils

import io.github.bananapuncher714.nbteditor.NBTEditor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.ItemMeta
import top.iseason.kotlin.deenchantment.DeEnchantmentWrapper
import top.iseason.kotlin.deenchantment.manager.ConfigManager

object EnchantTools {
    fun setDeEnchantLore(itemMeta: ItemMeta) {
        if (itemMeta is EnchantmentStorageMeta)
            setLoreWithEnchants(itemMeta, itemMeta.storedEnchants)
        else
            setLoreWithEnchants(itemMeta, itemMeta.enchants)
    }

    private fun clearEnchantLore(itemMeta: ItemMeta) {
        if (!itemMeta.hasLore()) return
        var loreList = itemMeta.lore!!
        val deEnchantmentsList = ConfigManager.getDeEnchantmentsNameList()
        for (name in deEnchantmentsList) {
            loreList = loreList.filterNot {
                it.contains(Regex("$name \\w+?"))
            }
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

    fun addEnchantments(target: ItemStack, en2: Map<Enchantment, Int>): Int {
        var cost = 0
        val itemMeta = target.itemMeta!!
        val enchantments: Map<Enchantment, Int> =
            if (itemMeta is EnchantmentStorageMeta)
                itemMeta.storedEnchants
            else
                target.enchantments
        val en1 = enchantments.toMutableMap()
        if (en2.isNullOrEmpty()) return 0
        for ((e2, l2) in en2) {
            if (target.type != Material.ENCHANTED_BOOK && !e2.canEnchantItem(target)) continue
            var isConflict = false
            for ((e1, _) in en1) {
//                println("${e2.key.key} is conflict ${e1.key.key} ? :${e2.conflictsWith(e1)}")
                if (e1 != e2 && e2.conflictsWith(e1)) {
                    isConflict = true
                    break
                }
            }
            if (isConflict) continue
            var level = l2
            if (en1.containsKey(e2)) {
                val nl2 = en1[e2]!!
                level = when {
                    nl2 == level -> level + 1
                    nl2 > level -> nl2
                    else -> level
                }
            }
            en1[e2] = level
            cost += level
        }

        addEnchants(itemMeta, en1)
        setDeEnchantLore(itemMeta)
        target.itemMeta = itemMeta
        return cost
    }

    private fun addEnchants(itemMeta: ItemMeta, ens: MutableMap<Enchantment, Int>) {
        if (ens.isNullOrEmpty()) return
        if (itemMeta is EnchantmentStorageMeta) {
            for ((en, le) in ens)
                itemMeta.addStoredEnchant(en, le, true)
        } else {
            for ((en, le) in ens)
                itemMeta.addEnchant(en, le, true)
        }
    }

    fun getRepairCost(item: ItemStack): Int {
        return if (NBTEditor.contains(item, "RepairCost")) {
            NBTEditor.getInt(item, "RepairCost")
        } else 0
    }

    fun setRepairCost(item: ItemStack, cost: Int): ItemStack {
        return NBTEditor.set(item, cost, "RepairCost")
    }

}