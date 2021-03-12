package top.iseason.kotlin.deenchantment.utils

import org.bukkit.inventory.meta.ItemMeta
import top.iseason.kotlin.deenchantment.DeEnchantmentWrapper
import top.iseason.kotlin.deenchantment.manager.ConfigManager

object EnchantTools {
    fun setDeEnchantLore(itemMeta: ItemMeta) {
        for ((enchant, level) in itemMeta.enchants) {
            if (enchant !is DeEnchantmentWrapper) continue
            val enchantmentName = ConfigManager.getEnchantmentName(enchant.name)!!
            val wholeName = "$enchantmentName ${Tools.intToRome(level)}"
            if (!itemMeta.hasLore()) {
                itemMeta.lore = listOf(wholeName)
            } else {
                val lores = itemMeta.lore!!
                var exist = false
                for ((count, lore) in lores.withIndex()) {
                    if (lore.contains(enchantmentName)) {
                        exist = true
                        lores[count] = wholeName
                    }
                }
                if (!exist)
                    itemMeta.lore?.add(wholeName)
            }
        }
//

    }
}