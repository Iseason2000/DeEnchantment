package top.iseason.kotlin.deenchantment.listeners.controllers

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import top.iseason.kotlin.deenchantment.utils.EnchantTools
import top.iseason.kotlin.deenchantment.utils.LogSender

class AnvilListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onPrepareAnvilEvent(event: PrepareAnvilEvent) {
        //2格为空则无响应
        val item1 = event.view.getItem(0) ?: return
        //可能是要改名
        val renameText = event.inventory.renameText

        if (renameText != null && renameText.isNotEmpty()) {
            val clone = item1.clone()
            val itemMeta = clone.itemMeta
            itemMeta?.setDisplayName(renameText)
            clone.itemMeta = itemMeta
            event.result = clone
        }
        val item2 = event.view.getItem(1) ?: return
        //空气没有ItemMeta
        val itemMeta2 = item2.itemMeta ?: return
        //1格为附魔书而2格不是附魔书
        if (item1.type == Material.ENCHANTED_BOOK && item2.type != Material.ENCHANTED_BOOK) return
        val enchantments2: Map<Enchantment, Int> =
            if (itemMeta2 is EnchantmentStorageMeta)
                itemMeta2.storedEnchants
            else
                item2.enchantments
        //第二个没有附魔跳过
        if (enchantments2.isEmpty()) {
            //修复物品判断
            val result = event.result ?: return
            //修复物品附魔
            result.addUnsafeEnchantments(item1.enchantments)
            return
        }

        //不是附魔书且材质与第一格不同
        if (itemMeta2 !is EnchantmentStorageMeta && item2.type != item1.type) return
        val resultItem = item1.clone()
        val cost = EnchantTools.addEnchantments(resultItem, enchantments2)
        if (item1 == resultItem) {//不能附魔的物品
            event.result = null
            return
        }
        val repairCost1 = EnchantTools.getRepairCost(item1)
        val repairCost2 = EnchantTools.getRepairCost(item2)
        val finalCost = if (repairCost1 <= repairCost2) repairCost2 else repairCost1
        val costItem = EnchantTools.setRepairCost(resultItem, 2 * finalCost + 1)
        val anvilView = event.inventory
        anvilView.repairCost = 2 * finalCost + 1 + cost
        if (anvilView.repairCost >= 40) {
            LogSender.log(
                anvilView.viewers[0],
                "${ChatColor.GREEN}本次附魔花费:${ChatColor.YELLOW} ${anvilView.repairCost}"
            )
        }
        if (renameText != null && renameText.isNotEmpty()) {
            val itemMeta = costItem.itemMeta
            itemMeta?.setDisplayName(renameText)
            costItem.itemMeta = itemMeta
        }
        event.result = costItem
    }
}