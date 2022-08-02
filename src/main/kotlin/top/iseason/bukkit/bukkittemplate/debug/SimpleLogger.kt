package top.iseason.bukkit.bukkittemplate.debug

import org.bukkit.Bukkit
import top.iseason.bukkit.bukkittemplate.TemplatePlugin
import top.iseason.bukkit.bukkittemplate.utils.toColor

/**
 * 输出日志
 */
fun info(message: Any?) {
    Bukkit.getConsoleSender().sendMessage((SimpleLogger.prefix + message).toColor())
}

/**
 * 输出debug日志,只有当 SimpleLogger.isDebug 为true 时有效
 */
fun debug(message: Any?) {
    if (SimpleLogger.isDebug)
        info(message)
}

/**
 * 输出警告日志
 */
fun warn(message: Any?) {
    TemplatePlugin.getPlugin().logger.warning(message.toString())
}

object SimpleLogger {
    var prefix = "[${TemplatePlugin.getPlugin().description.name}] "
    var isDebug = false
}
