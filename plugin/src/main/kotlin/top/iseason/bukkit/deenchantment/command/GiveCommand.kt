package top.iseason.bukkit.deenchantment.command

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.permissions.PermissionDefault
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantmentWrapper
import top.iseason.bukkit.deenchantment.settings.Message
import top.iseason.bukkit.deenchantment.utils.EnchantTools
import top.iseason.bukkittemplate.command.CommandNode
import top.iseason.bukkittemplate.command.CommandNodeExecutor
import top.iseason.bukkittemplate.command.Param
import top.iseason.bukkittemplate.command.ParamSuggestCache
import top.iseason.bukkittemplate.utils.bukkit.EntityUtils.giveItems
import top.iseason.bukkittemplate.utils.bukkit.ItemUtils.applyMeta
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.formatBy
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.noColor
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.sendColorMessage
import top.iseason.bukkittemplate.utils.other.NumberUtils.toRoman
import top.iseason.bukkittemplate.utils.other.submit

object GiveCommand : CommandNode(
    name = "give",
    default = PermissionDefault.OP,
    description = "给予玩家特定负魔书",
    params = listOf(
        Param("[player]", suggestRuntime = ParamSuggestCache.playerParam), Param("[name]") {
            BaseEnchant.enchants.filter { it.enable }.map { it.translateName.noColor() }
        }, Param("<level>", listOf("1", "2", "3", "4", "5"))
    ),
    async = true
) {
    override var onExecute: CommandNodeExecutor? = CommandNodeExecutor { params, sender ->
        val player = params.next<Player>()
        val en = params.next<DeEnchantmentWrapper>()
        val level = params.nextOrNull<Int>() ?: 1
        val item = ItemStack(Material.ENCHANTED_BOOK).applyMeta {
            val m = this as EnchantmentStorageMeta
            m.addStoredEnchant(en, level, false)
            EnchantTools.updateLore(m)
        }
        submit {
            player.giveItems(item)
        }
        sender.sendColorMessage(
            Message.command__give.formatBy(
                player.name,
                en.translateName.noColor() + " " + level.toRoman()
            )
        )
    }

}