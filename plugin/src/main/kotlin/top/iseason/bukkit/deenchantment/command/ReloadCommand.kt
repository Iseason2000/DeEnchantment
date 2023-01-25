package top.iseason.bukkit.deenchantment.command

import org.bukkit.command.CommandSender
import org.bukkit.permissions.PermissionDefault
import top.iseason.bukkit.deenchantment.settings.Config
import top.iseason.bukkit.deenchantment.settings.Message
import top.iseason.bukkittemplate.command.CommandNode
import top.iseason.bukkittemplate.command.CommandNodeExecutor
import top.iseason.bukkittemplate.command.Params
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.sendColorMessage

object ReloadCommand : CommandNode(
    name = "reload",
    default = PermissionDefault.OP,
    description = "重新注册负魔",
    async = true
) {
    override var onExecute: CommandNodeExecutor? = object : CommandNodeExecutor {
        override fun onExecute(params: Params, sender: CommandSender) {
            try {
                Config.reload()
                sender.sendColorMessage(Message.command__reload_success)
            } catch (e: Exception) {
                e.printStackTrace()
                sender.sendColorMessage(Message.command__reload_failure)
            }
        }
    }
}