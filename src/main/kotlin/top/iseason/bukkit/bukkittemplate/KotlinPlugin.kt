package top.iseason.bukkit.bukkittemplate

import org.bukkit.Bukkit
import org.bukkit.event.Listener

open class KotlinPlugin {

    /**
     * 获取bukkit插件对象,在onLoad阶段才会被赋值
     */
    lateinit var javaPlugin: TemplatePlugin

    /**
     * 在其他线程加载，比onEnable先调用,结束了才调用onEnable
     */
    open fun onAsyncLoad() {}

    /**
     * 在插件启用后运行
     */
    open fun onEnable() {}

    /**
     * 在插件启用后异步运行
     */
    open fun onAsyncEnable() {}

    /**
     * 在插件停用时运行
     */
    open fun onDisable() {}

    /**
     * 快速注册监听器
     */
    fun registerListeners(listener: Listener) {
        Bukkit.getPluginManager().registerEvents(listener, javaPlugin)
    }
}