package top.iseason.bukkit.deenchantment.command

import org.bukkit.entity.Player
import org.bukkit.permissions.PermissionDefault
import top.iseason.bukkit.deenchantment.settings.Message
import top.iseason.bukkit.deenchantment.utils.EnchantTools
import top.iseason.bukkittemplate.command.CommandNode
import top.iseason.bukkittemplate.command.CommandNodeExecutor
import top.iseason.bukkittemplate.command.ParmaException
import top.iseason.bukkittemplate.utils.bukkit.ItemUtils.applyMeta
import top.iseason.bukkittemplate.utils.bukkit.ItemUtils.checkAir
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.sendColorMessage

object UpdateCommand : CommandNode(
    name = "update",
    default = PermissionDefault.OP,
    description = "更新某玩家手上装备的负魔描述及名称",
    async = true
) {
    override var onExecute: CommandNodeExecutor? = CommandNodeExecutor { params, sender ->
        val player = params.nextOrNull<Player>() ?: sender as? Player ?: throw ParmaException("请指定一个玩家!")
        val itemInMainHand = player.inventory.itemInMainHand
        if (itemInMainHand.checkAir()) {
            throw ParmaException(Message.command__update_hand)
        }
        itemInMainHand.applyMeta {
            EnchantTools.updateLore(this)
        }
        sender.sendColorMessage(Message.command__update)
    }
}
