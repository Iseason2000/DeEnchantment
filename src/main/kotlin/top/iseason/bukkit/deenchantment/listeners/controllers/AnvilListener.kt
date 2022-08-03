package top.iseason.bukkit.deenchantment.listeners.controllers

import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.Repairable
import top.iseason.bukkit.bukkittemplate.utils.bukkit.applyMeta
import top.iseason.bukkit.bukkittemplate.utils.bukkit.checkAir
import top.iseason.bukkit.bukkittemplate.utils.formatBy
import top.iseason.bukkit.bukkittemplate.utils.sendColorMessage
import top.iseason.bukkit.deenchantment.manager.DeEnchantmentWrapper
import top.iseason.bukkit.deenchantment.settings.Config
import top.iseason.bukkit.deenchantment.settings.Message
import top.iseason.bukkit.deenchantment.utils.EnchantTools

object AnvilListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPrepareAnvilEvent(event: PrepareAnvilEvent) {
        //2格为空则无响应
        val item1 = event.view.getItem(0) ?: return
        if (item1.type.checkAir()) return
        val item2 = event.view.getItem(1) ?: return
        //其他插件使用铁砧,仅更新lore
        val renameText = event.inventory.renameText
        //可能是要改名
        if (item2.type.checkAir() && !renameText.isNullOrEmpty()) {
            val clone = item1.itemMeta
            clone?.setDisplayName(renameText)
            event.result?.itemMeta = clone
            return
        }
        if (item2.type.checkAir()) return
        val itemMeta1 = item1.itemMeta ?: return
        val itemMeta2 = item2.itemMeta ?: return
//        //1格为附魔书而2格不是附魔书
        if (item1.type == Material.ENCHANTED_BOOK && item2.type != Material.ENCHANTED_BOOK) return
        val en2: MutableMap<Enchantment, Int> =
            if (itemMeta2 is EnchantmentStorageMeta)
                itemMeta2.storedEnchants
            else
                item2.enchantments
//        //第二个没有附魔跳过
        if (en2.isEmpty()) {
            //修复物品判断
            val result = event.result ?: return
            //修复物品附魔
            //补回附魔
            result.applyMeta {
                itemMeta1.enchants.forEach { (t, u) ->
                    if (t !is DeEnchantmentWrapper) return@forEach
                    if (this is EnchantmentStorageMeta)
                        addStoredEnchant(t, u, true)
                    else
                        addEnchant(t, u, true)
                }
                EnchantTools.updateLore(this)
            }
            return
        }
        val ignoreConflicts = event.view.player.gameMode == GameMode.CREATIVE
        //不是附魔书且材质与第一格不同
        if (itemMeta2 !is EnchantmentStorageMeta && item2.type != item1.type) return
        val itemClone = item1.clone()
        val removes = mutableSetOf<Enchantment>()
        val cost =
            EnchantTools.addEnchantments(itemClone, en2, removes, ignoreConflicts)
        var result = event.result
        val anvilView = event.inventory
        //不能附魔的物品
        if (item1 == result) {
            event.result = null
            return
        }
        if (result == null) {
            result = itemClone.applyMeta {
                if (this !is Repairable) return
                repairCost += 1
                if (!renameText.isNullOrEmpty()) setDisplayName(renameText)
            }
        } else {
            result = result.applyMeta {
                //删除冲突的
                for (remove in removes) {
                    removeEnchant(remove)
                }
                itemClone.enchantments.forEach { (t, u) ->
                    if (t !is DeEnchantmentWrapper) return@forEach
                    if (this is EnchantmentStorageMeta)
                        addStoredEnchant(t, u, true)
                    else
                        addEnchant(t, u, true)
                }
                EnchantTools.updateLore(this)
                if (!renameText.isNullOrEmpty()) setDisplayName(renameText)
            }
        }
        if (itemMeta1 !is EnchantmentStorageMeta && result.enchantments == item1.enchantments) {
            event.result = null
            return
        }
        anvilView.repairCost += cost.coerceAtLeast(1)
        if (anvilView.repairCost >= 40) {
            if (!Config.tooExpensive && !ignoreConflicts) {
                event.result = null
                return
            }
            anvilView.viewers.firstOrNull()
                ?.sendColorMessage(Message.anvil_cost.formatBy(anvilView.repairCost))
        } else
            event.result = result
    }
}

