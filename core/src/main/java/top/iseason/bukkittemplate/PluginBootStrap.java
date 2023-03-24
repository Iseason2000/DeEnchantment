package top.iseason.bukkittemplate;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import top.iseason.bukkittemplate.hook.BungeeCordHook;
import top.iseason.bukkittemplate.hook.PlaceHolderHook;

/**
 * 插件启动代理类，由自定义 ClassLoader加载
 */
public final class PluginBootStrap {
    private BukkitPlugin bukkitPlugin;
    private JavaPlugin javaPlugin;

    private PluginBootStrap() {
    }

    private void onLoad(Float ignore) {
//        ReflectionUtil.enable();
        // 加载阶段如果报错直接中断加载
        bukkitPlugin.onLoad();
    }

    private void onEnable(Boolean ignore) {
        //启动阶段允许报错
        try {
            PlaceHolderHook.INSTANCE.checkHooked();
            BungeeCordHook.onEnable();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        try {
            bukkitPlugin.onEnable();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        Bukkit.getScheduler().runTaskAsynchronously(javaPlugin, this::onAsyncEnabled);
    }

    private void onDisable(Double ignore) {
        try {
            bukkitPlugin.onDisable();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bukkit.getScheduler().cancelTasks(javaPlugin);
        HandlerList.unregisterAll(javaPlugin);
        DisableHook.disableAll();
        BungeeCordHook.onDisable();
    }

    private void onAsyncEnabled() {
        try {
            bukkitPlugin.onAsyncEnable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
