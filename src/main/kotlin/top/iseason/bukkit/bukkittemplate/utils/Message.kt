package top.iseason.bukkit.bukkittemplate.utils

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import java.lang.reflect.Method
import java.util.regex.Pattern

/**
 * 发送带颜色转换的消息,不输出null与空string
 */
fun CommandSender.sendColorMessage(message: Any?) {
    if (message == null || message.toString().isEmpty()) return
    sendMessage(message.toString().toColor())
}

/**
 * 发送带颜色转换的消息
 */
fun CommandSender.sendColorMessages(vararg messages: String) = messages.forEach { sendColorMessage(it) }

/**
 * 发送带颜色转换的消息
 */
fun CommandSender.sendColorMessages(messages: Collection<String>?) = messages?.forEach { sendColorMessage(it) }

fun CommandSender.sendMessage(message: Any?) {
    if (message == null || message.toString().isEmpty()) return
    sendMessage(message.toString())
}

/**
 * 进行颜色转换并发送给所有人
 */
fun broadcast(message: Any?) = Bukkit.getOnlinePlayers().forEach { it.sendColorMessage(message) }

/**
 * 进行颜色转换并发送给控制台
 */
fun sendConsole(message: Any?) = Bukkit.getConsoleSender().sendColorMessage(message)

/**
 * 进行颜色转换并发送给控制台
 */
fun sendConsole(messages: Collection<String>?) = messages?.forEach { sendConsole(it) }

/**
 * 进行颜色转换并发送给控制台
 */
fun sendConsole(messages: Array<String>?) = messages?.forEach { sendConsole(it) }

object Message {
    val colorPattern: Pattern = Pattern.compile("#[A-F|\\d]{6}", Pattern.CASE_INSENSITIVE)
    var rgbColorMethod: Method? = null
        private set

    init {
        try {
            rgbColorMethod = net.md_5.bungee.api.ChatColor::class.java.getMethod("of", String::class.java)
        } catch (_: Exception) {
        }
    }
}

/**
 * 将String转为bukkit支持的颜色消息
 */
fun String.toColor(): String {
    var temp = this
    //如果支持16进制
    if (Message.rgbColorMethod != null) {
        val matcher = Message.colorPattern.matcher(temp)
        while (matcher.find()) {
            val rgbColor = matcher.group()
            val chatColor = Message.rgbColorMethod!!.invoke(null, rgbColor)
            temp = temp.replace(rgbColor, chatColor.toString())
        }
    }
    return ChatColor.translateAlternateColorCodes('&', temp)
}

fun String.noColor(): String? = ChatColor.stripColor(this)