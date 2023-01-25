package top.iseason.bukkittemplate.debug

import org.bukkit.Bukkit
import top.iseason.bukkittemplate.BukkitTemplate
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.noColor
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.toColor

/**
 * 输出日志
 */
fun info(message: Any?) {
    Bukkit.getConsoleSender().sendMessage((SimpleLogger.prefix + message).toColor())
}

/**
 * 输出debug日志,只有当 SimpleLogger.isDebug 为true 时输出
 */
fun debug(message: Any?) {
    if (SimpleLogger.isDebug)
        info(message)
}

/**
 * 输出警告日志
 */
fun warn(message: Any?) {
    BukkitTemplate.getPlugin().logger.warning(SimpleLogger.prefix + message.toString().toColor().noColor())
}

/**
 * 日志配置类
 */
object SimpleLogger {
    /**
     * 消息前缀
     */
    var prefix = "&a[&6${BukkitTemplate.getPlugin().description.name}&a] &f"

    /**
     * 是否是debug模式
     */
    var isDebug = false
}