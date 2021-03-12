package top.iseason.kotlin.deenchantment.utils

import org.bukkit.Bukkit
import org.bukkit.ChatColor.*

object LogSender {
    fun log(message: String) {
        Bukkit.getConsoleSender().sendMessage(
            "${LIGHT_PURPLE}[${RED}负魔书${LIGHT_PURPLE}] ${RESET}$message"
        )
    }

}