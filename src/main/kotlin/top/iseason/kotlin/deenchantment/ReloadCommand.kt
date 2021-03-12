package top.iseason.kotlin.deenchantment

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import top.iseason.kotlin.deenchantment.manager.ConfigManager

class ReloadCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!sender.isOp)return true
        ConfigManager.reload()
        return true
    }
}