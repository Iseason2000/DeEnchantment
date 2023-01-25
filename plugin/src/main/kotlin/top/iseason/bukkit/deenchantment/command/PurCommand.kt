package top.iseason.bukkit.deenchantment.command

import org.bukkit.entity.Player
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.permissions.PermissionDefault
import top.iseason.bukkit.deenchantment.manager.DeEnchantmentWrapper
import top.iseason.bukkit.deenchantment.settings.Message
import top.iseason.bukkit.deenchantment.utils.EnchantTools
import top.iseason.bukkittemplate.command.CommandNode
import top.iseason.bukkittemplate.command.CommandNodeExecutor
import top.iseason.bukkittemplate.command.Param
import top.iseason.bukkittemplate.command.ParamSuggestCache
import top.iseason.bukkittemplate.utils.bukkit.ItemUtils.applyMeta
import top.iseason.bukkittemplate.utils.bukkit.ItemUtils.checkAir
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.sendColorMessage

object PurCommand : CommandNode(
    name = "purification",
    default = PermissionDefault.OP,
    description = "将玩家手上物品的负魔转为正常的附魔",
    async = true,
    params = listOf(Param("[player]", suggestRuntime = ParamSuggestCache.playerParam))
) {
    override var onExecute: CommandNodeExecutor? = CommandNodeExecutor { params, sender ->
        val player = params.next<Player>()
        val itemInMainHand = player.inventory.itemInMainHand
        if (itemInMainHand.checkAir()) return@CommandNodeExecutor
        itemInMainHand.applyMeta {
            val itemMeta = this
            if (itemMeta is EnchantmentStorageMeta) {
                for ((enchant, level) in itemMeta.storedEnchants) {
                    if (enchant !is DeEnchantmentWrapper) continue
                    itemMeta.removeStoredEnchant(enchant)
                    itemMeta.addStoredEnchant(enchant.getEnchantment(), level, true)
                }
            } else {
                for ((enchant, level) in itemMeta.enchants) {
                    if (enchant !is DeEnchantmentWrapper) continue
                    itemMeta.removeEnchant(enchant)
                    itemMeta.addEnchant(enchant.getEnchantment(), level, true)
                }
            }
            EnchantTools.updateLore(itemMeta)
        }
        sender.sendColorMessage(Message.command__purification_success)
    }

}