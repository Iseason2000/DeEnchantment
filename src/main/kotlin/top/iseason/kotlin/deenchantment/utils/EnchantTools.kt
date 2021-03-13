package top.iseason.kotlin.deenchantment.utils

import org.bukkit.enchantments.Enchantment
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
            loreList = loreList.filterNot { it.matches(Regex("$name \\w+")) }
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

}