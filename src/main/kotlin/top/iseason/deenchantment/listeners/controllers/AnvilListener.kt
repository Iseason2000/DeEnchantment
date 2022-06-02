package top.iseason.deenchantment.listeners.controllers

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.Repairable
import top.iseason.deenchantment.manager.ConfigManager
import top.iseason.deenchantment.manager.DeEnchantmentWrapper
import top.iseason.deenchantment.utils.EnchantTools
import top.iseason.deenchantment.utils.LogSender

class AnvilListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPrepareAnvilEvent(event: PrepareAnvilEvent) {
        //2格为空则无响应
        val item1 = event.view.getItem(0) ?: return
        val item2 = event.view.getItem(1) ?: return
        //其他插件使用铁砧,仅更新lore
        if (ConfigManager.getConfig().getBoolean("AnvilConflict")) {
            Bukkit.getScheduler().runTask(ConfigManager.getPlugin(), Runnable {
                val clone = event.inventory.getItem(2)?.clone() ?: return@Runnable
                val itemMeta = clone.itemMeta ?: return@Runnable
                EnchantTools.setDeEnchantLore(itemMeta)
                clone.itemMeta = itemMeta
                event.result = clone
                event.inventory.setItem(2, clone)
            })
            return
        }
        val renameText = event.inventory.renameText
        //可能是要改名
        if (item2.type.isAir && renameText != null && renameText.isNotEmpty()) {
            val clone = item1.clone()
            val itemMeta = clone.itemMeta
            itemMeta?.setDisplayName(renameText)
            clone.itemMeta = itemMeta
            event.result = clone
            return
        }
        if (item1.type.isAir || item2.type.isAir) return

        //空气没有ItemMeta
        val itemMeta1 = item1.itemMeta ?: return
        val itemMeta2 = item2.itemMeta ?: return
        //1格为附魔书而2格不是附魔书
        if (item1.type == Material.ENCHANTED_BOOK && item2.type != Material.ENCHANTED_BOOK) return
        val enchantments2: MutableMap<Enchantment, Int> =
            (if (itemMeta2 is EnchantmentStorageMeta)
                itemMeta2.storedEnchants
            else
                item2.enchantments).toMutableMap()
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
        if (event.result != null) {
            item1.enchantments.forEach { (t, u) ->
                if (t !is DeEnchantmentWrapper) return@forEach
                enchantments2[t] = u
            }
        }
        val resultItem = event.result?.clone() ?: item1.clone()
        val level =
            EnchantTools.addEnchantments(resultItem, enchantments2, event.view.player.gameMode == GameMode.CREATIVE)
        if (item1 == resultItem) {//不能附魔的物品
            event.result = null
            return
        }
        val repairCost1 = if (itemMeta1 is Repairable) itemMeta1.repairCost else 0
        val repairCost2 = if (itemMeta2 is Repairable) itemMeta2.repairCost else 0
        val finalCost = if (repairCost1 <= repairCost2) repairCost2 else repairCost1
        with(resultItem) {
            val meta = itemMeta
            if (meta is Repairable) {
                meta.repairCost = repairCost1 + 1
                itemMeta = meta
            }
        }
        val anvilView = event.inventory
        val cost = ConfigManager.expression.evaluate(
            ConfigManager.expressionStr.replace("{repair}", finalCost.toString())
                .replace("{level}", level.toString())
        ).toInt()
        anvilView.repairCost = cost
        if (anvilView.repairCost >= 40) {
            val config = ConfigManager.getConfig()
            if (config.contains("AllowTooExpensive") && !config.getBoolean("AllowTooExpensive"))
                return
            LogSender.log(
                anvilView.viewers[0],
                "${ChatColor.GREEN}本次附魔花费:${ChatColor.YELLOW} ${anvilView.repairCost}"
            )
        }
        if (renameText != null && renameText.isNotEmpty()) {
            val itemMeta = resultItem.itemMeta
            itemMeta?.setDisplayName(renameText)
            resultItem.itemMeta = itemMeta
        }
        event.result = resultItem
    }
}