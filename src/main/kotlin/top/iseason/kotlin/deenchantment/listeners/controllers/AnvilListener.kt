package top.iseason.kotlin.deenchantment.listeners.controllers

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import top.iseason.kotlin.deenchantment.utils.EnchantTools

//todo:不支持下界合金及部分装备
class AnvilListener : Listener {
    @EventHandler
    fun onPrepareAnvilEvent(event: PrepareAnvilEvent) {
        val item1 = event.view.getItem(0)
        val item2 = event.view.getItem(1)
        if (item1 == null || item1.type == Material.AIR) return
        if (item2 == null || item2.type == Material.AIR) return
        if (item1.type == Material.ENCHANTED_BOOK && item2.type != Material.ENCHANTED_BOOK) return
        val result = event.result
//        println(DeEnchantment.DE_binding_curse.canEnchantItem(item1))
        if ((item2.type != Material.ENCHANTED_BOOK) && (result == null || result.type == Material.AIR)) {
            if (!(item2.type == item2.type && item2.enchantments.isNotEmpty()))
                return
        }
        val anvilView = event.inventory
        val itemMeta2 = item2.itemMeta
        val enchantments2: Map<Enchantment, Int> =
            if (itemMeta2 is EnchantmentStorageMeta)
                itemMeta2.storedEnchants
            else
                item2.enchantments
        val resultItem = item1.clone()
        val cost = EnchantTools.addEnchantments(resultItem, enchantments2)
        if (item1 == resultItem) return

        val repairCost1 = EnchantTools.getRepairCost(item1)
        val repairCost2 = EnchantTools.getRepairCost(item2)
        val finalCost = if (repairCost1 < repairCost2) repairCost2 else repairCost1
        val costItem = EnchantTools.setRepairCost(resultItem, 2 * finalCost + 1)
        anvilView.repairCost = 2 * finalCost + 1 + cost
        event.result = costItem
    }
}