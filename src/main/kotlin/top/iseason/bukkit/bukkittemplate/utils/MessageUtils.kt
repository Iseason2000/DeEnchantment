package top.iseason.bukkit.bukkittemplate.utils

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import java.lang.reflect.Method
import java.util.regex.Pattern

/**
 * 发送带颜色转换的消息,不输出null与空string
 */
fun CommandSender.sendColorMessage(message: Any?, prefix: String = MessageUtils.defaultPrefix) {
    if (message == null || message.toString().isEmpty()) return
    sendMessage("$prefix$message".toColor())
}

/**
 * 发送带颜色转换的消息
 */
fun CommandSender.sendColorMessages(vararg messages: String, prefix: String = MessageUtils.defaultPrefix) =
    messages.forEach { sendColorMessage(it, prefix) }

/**
 * 发送带颜色转换的消息
 */
fun CommandSender.sendColorMessages(messages: Collection<String>?, prefix: String = MessageUtils.defaultPrefix) =
    messages?.forEach { sendColorMessage(it, prefix) }

fun CommandSender.sendMessage(message: Any?, prefix: String = MessageUtils.defaultPrefix) {
    if (message == null || message.toString().isEmpty()) return
    sendMessage(message.toString(), prefix)
}

/**
 * 进行颜色转换并发送给所有人
 */
fun broadcast(message: Any?, prefix: String = MessageUtils.defaultPrefix) =
    Bukkit.getOnlinePlayers().forEach { it.sendColorMessage(message, prefix) }

/**
 * 进行颜色转换并发送给控制台
 */
fun sendConsole(message: Any?, prefix: String = MessageUtils.defaultPrefix) =
    Bukkit.getConsoleSender().sendColorMessage(message, prefix)

/**
 * 进行颜色转换并发送给控制台
 */
fun sendConsole(messages: Collection<String>?, prefix: String = MessageUtils.defaultPrefix) =
    messages?.forEach { sendConsole(it, prefix) }

/**
 * 进行颜色转换并发送给控制台
 */
fun sendConsole(messages: Array<String>?, prefix: String = MessageUtils.defaultPrefix) =
    messages?.forEach { sendConsole(it, prefix) }

object MessageUtils {
    var defaultPrefix = ""
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
    if (MessageUtils.rgbColorMethod != null) {
        val matcher = MessageUtils.colorPattern.matcher(temp)
        while (matcher.find()) {
            val rgbColor = matcher.group()
            val chatColor = MessageUtils.rgbColorMethod!!.invoke(null, rgbColor)
            temp = temp.replace(rgbColor, chatColor.toString())
        }
    }
    return ChatColor.translateAlternateColorCodes('&', temp)
}

fun String.noColor(): String? = ChatColor.stripColor(this)

fun String.formatBy(vararg values: Any?): String {
    var temp = this
    values.forEachIndexed { index, any ->
        if (any == null) return@forEachIndexed
        temp = temp.replace("{$index}", any.toString())
    }
    return temp
}