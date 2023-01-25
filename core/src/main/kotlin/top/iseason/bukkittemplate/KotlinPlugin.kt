package top.iseason.bukkittemplate

/**
 * kotlin插件的入口
 * 请使用 object 单例继承本类,一个插件只能有一个 KotlinPlugin 单例
 * 例如 object TemplatePlugin : KotlinPlugin()
 */
abstract class KotlinPlugin {

    /**
     * 获取bukkit插件对象,在onLoad阶段才会被赋值
     */
    val javaPlugin = BukkitTemplate.getPlugin()

    /**
     * 在其他线程加载，比onEnable先调用,结束了才调用onEnable
     */
    open fun onLoad() {}

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


}