package top.iseason.kotlin.deenchantment.listeners

import io.github.bananapuncher714.nbteditor.NBTEditor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.Listener
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.AnvilInventory
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import top.iseason.kotlin.deenchantment.utils.EnchantTools

class AnvilListener : Listener {
    @EventHandler
    fun onPrepareAnvilEvent(event: PrepareAnvilEvent) {
        val item1 = event.view.getItem(0)
        val item2 = event.view.getItem(1)
        if (item1 == null || item1.type == Material.AIR) return
        if (item2 == null || item2.type == Material.AIR) return
        if (item1.type == Material.ENCHANTED_BOOK && item2.type != Material.ENCHANTED_BOOK) return
        val itemMeta1 = item1.itemMeta
        val enchantments1: Map<Enchantment, Int> =
            if (itemMeta1 is EnchantmentStorageMeta)
                itemMeta1.storedEnchants
            else
                item1.enchantments
        val itemMeta2 = item2.itemMeta
        val enchantments2: Map<Enchantment, Int> =
            if (itemMeta2 is EnchantmentStorageMeta)
                itemMeta2.storedEnchants
            else
                item2.enchantments
        val result = EnchantTools.combineEnchantments(enchantments1, enchantments2)
        val resultItem = item1.clone()
        resultItem.addUnsafeEnchantments(result)
        val resultItemMeta = resultItem.itemMeta!!
        EnchantTools.setDeEnchantLore(resultItemMeta)
        resultItem.itemMeta = resultItemMeta
        val repairCost1 = EnchantTools.getRepairCost(item1)
        val repairCost2 = EnchantTools.getRepairCost(item2)
        val finalCost = if (repairCost1 < repairCost2) repairCost2 else repairCost1
        val costItem = EnchantTools.setRepairCost(resultItem, 2 * finalCost + 1)
        val anvilView = event.inventory
        anvilView.repairCost = 2 * finalCost + 1//todo 正确的附魔花费
        event.result = costItem

    }
}