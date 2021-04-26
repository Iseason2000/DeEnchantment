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
        //2格为空则无响应
        val item1 = event.view.getItem(0) ?: return
        val item2 = event.view.getItem(1) ?: return
        //空气没有ItemMeta
        val itemMeta1 = item1.itemMeta ?: return
        val itemMeta2 = item2.itemMeta ?: return
        //1格为附魔书而2格不是附魔书
        if (item1.type == Material.ENCHANTED_BOOK && item2.type != Material.ENCHANTED_BOOK) return
        val enchantments2: Map<Enchantment, Int> =
            if (itemMeta2 is EnchantmentStorageMeta)
                itemMeta2.storedEnchants
            else
                item2.enchantments
        //第二个没有附魔跳过
        if (enchantments2.isEmpty()) return
        //不是附魔书且材质与第一格不同
        if (itemMeta2 !is EnchantmentStorageMeta && itemMeta2 != itemMeta1) return
        val resultItem = item1.clone()
        val cost = EnchantTools.addEnchantments(resultItem, enchantments2)
        if (item1 == resultItem) return//todo:检查必要性
        val repairCost1 = EnchantTools.getRepairCost(item1)
        val repairCost2 = EnchantTools.getRepairCost(item2)
        val finalCost = if (repairCost1 <= repairCost2) repairCost2 else repairCost1
        val costItem = EnchantTools.setRepairCost(resultItem, 2 * finalCost + 1)
        val anvilView = event.inventory
        anvilView.repairCost = 2 * finalCost + 1 + cost
        event.result = costItem
    }
}