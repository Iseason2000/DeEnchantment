package top.iseason.kotlin.deenchantment.utils

import org.bukkit.Bukkit
import org.bukkit.ChatColor.*
import org.bukkit.command.CommandSender

object LogSender {
    fun consoleLog(message: String) {
        Bukkit.getConsoleSender().sendMessage(
            "${LIGHT_PURPLE}[${RED}负魔书${LIGHT_PURPLE}] ${RESET}$message"
        )
    }

    fun log(sender: CommandSender, message: String) {
        sender.sendMessage("${LIGHT_PURPLE}[${RED}负魔书${LIGHT_PURPLE}] ${RESET}$message")
    }

}