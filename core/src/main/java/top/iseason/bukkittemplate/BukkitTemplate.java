package top.iseason.bukkittemplate;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import top.iseason.bukkittemplate.dependency.PluginDependency;
import top.iseason.bukkittemplate.hook.PlaceHolderHook;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.jar.JarFile;

/**
 * bukkit插件主类/入口
 */
public class BukkitTemplate extends JavaPlugin {

    private static List<Class<?>> classes;
    private static KotlinPlugin ktPlugin;
    private static BukkitTemplate plugin = null;

    private static boolean offlineLibInstalled = false;

    /**
     * 构造方法，负责下载/添加依赖，并启动插件
     */
    public BukkitTemplate() {
        plugin = this;
        offlineLibInstalled = Bukkit.getPluginManager().getPlugin("IseasonOfflineLib") != null || Bukkit.getPluginManager().getPlugin("eco") != null;
        if (!offlineLibInstalled && !PluginDependency.parsePluginYml()) {
            throw new RuntimeException("Loading dependencies error! please check your network!");
        }
        classes = loadClass();
        ktPlugin = findInstance();
    }


    /**
     * 寻找插件主类单例
     */
    private static KotlinPlugin findInstance() {
        for (Class<?> aClass : classes) {
            if (KotlinPlugin.class.isAssignableFrom(aClass)) {
                try {
                    Field instance = aClass.getDeclaredField("INSTANCE");
                    instance.setAccessible(true);
                    return (KotlinPlugin) instance.get(null);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        throw new RuntimeException("can not find plugin instance! you need a object class implement KotlinPlugin");
    }

    /**
     * 加载需要的class
     *
     * @return 需要的class的集合
     */
    private static List<Class<?>> loadClass() {
        ArrayList<Class<?>> classes = new ArrayList<>();
        //猜測程序入口以减少遍历的消耗
        String canonicalName = BukkitTemplate.class.getCanonicalName();
        String guessName = canonicalName.replace(".libs.core.BukkitTemplate", "") + "." + plugin.getName();
        try {
            Class<?> aClass = Class.forName(guessName, false, BukkitTemplate.class.getClassLoader());
            if (KotlinPlugin.class.isAssignableFrom(aClass)) {
                classes.add(aClass);
                return classes;
            }
        } catch (ClassNotFoundException ignored) {
        }
        URL location = BukkitTemplate.class.getProtectionDomain().getCodeSource().getLocation();

        File srcFile;
        try {
            srcFile = new File(location.toURI());
        } catch (URISyntaxException e) {
            try {
                URI uri = ((JarURLConnection) location.openConnection()).getJarFileURL().toURI();
                srcFile = new File(uri);
            } catch (URISyntaxException | IOException ex) {
                srcFile = new File(location.getPath());
            }
        }
        try (JarFile jarFile = new JarFile(srcFile)) {
            jarFile.stream().forEach((it) -> {
                String name = it.getName();
                if (!name.endsWith(".class") || name.startsWith("META-INF")) {
                    return;
                }
                Class<?> aClass;
                try {
                    aClass = Class.forName(name.replace('/', '.').substring(0, name.length() - 6), false, BukkitTemplate.class.getClassLoader());
                } catch (ClassNotFoundException e) {
                    return;
                }
                if (KotlinPlugin.class.isAssignableFrom(aClass) && KotlinPlugin.class != aClass) {
                    classes.add(aClass);
                }
            });
        } catch (IOException ignored) {
        }
        return classes;
    }

    /**
     * 获取Bukkit插件主类
     *
     * @return Bukkit插件主类
     */
    public static BukkitTemplate getPlugin() {
        return plugin;
    }

    /**
     * 获取插件主类
     *
     * @return 插件主类
     */
    public static KotlinPlugin getKtPlugin() {
        return ktPlugin;
    }

    public static boolean isOfflineLibInstalled() {
        return offlineLibInstalled;
    }

    // 比 onEnabled 先调用
    public void onAsyncLoad() {
        ktPlugin.onLoad();
    }

    @Override
    public void onLoad() {
        ktPlugin.onLoad();
    }

    public void onAsyncEnabled() {
        ktPlugin.onAsyncEnable();
    }

    @Override
    public void onEnable() {
        PlaceHolderHook.INSTANCE.checkHooked();
        ktPlugin.onEnable();
        CompletableFuture.runAsync(this::onAsyncEnabled);
    }

    @Override
    public void onDisable() {
        if (ktPlugin != null)
            ktPlugin.onDisable();
        Bukkit.getScheduler().cancelTasks(this);
        HandlerList.unregisterAll(this);
        DisableHook.disableAll();
    }
}
