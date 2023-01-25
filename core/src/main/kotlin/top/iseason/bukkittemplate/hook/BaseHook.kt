package top.iseason.bukkittemplate.hook

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin

/**
 * 软依赖钩子,需要传入依赖的名字
 */
abstract class BaseHook(val pluginName: String) {

    private var plugin = Bukkit.getPluginManager().getPlugin(pluginName)

    /**
     * 获取插件入口
     */
    fun getInstance(): Plugin {
        check(plugin != null) { "依赖 $pluginName 不存在, 你应该使用 hasHooked 方法检查一下再调用!" }
        return plugin!!
    }

    /**
     * 检查是否存在软依赖
     */
    val hasHooked get() = plugin != null

    /**
     * 检查软依赖是否存在
     */
    fun checkHooked() {
        plugin = Bukkit.getPluginManager().getPlugin(pluginName)
    }

}