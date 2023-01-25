package top.iseason.bukkittemplate.command

import org.bukkit.command.CommandSender

interface CommandNodeExecutor {
    fun onExecute(params: Params, sender: CommandSender)
}