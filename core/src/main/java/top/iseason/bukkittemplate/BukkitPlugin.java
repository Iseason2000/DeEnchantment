package top.iseason.bukkittemplate;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * 框架插件入口
 */
public interface BukkitPlugin {

    default JavaPlugin getJavaPlugin() {
        return BukkitTemplate.getPlugin();
    }

    /**
     * 在其他线程加载，比onEnable先调用,结束了才调用onEnable
     */
    default void onLoad() {
    }

    /**
     * 在插件启用后运行
     */
    default void onEnable() {
    }

    /**
     * 在插件启用后异步运行
     */
    default void onAsyncEnable() {
    }

    /**
     * 在插件停用时运行
     */
    default void onDisable() {
    }
}
