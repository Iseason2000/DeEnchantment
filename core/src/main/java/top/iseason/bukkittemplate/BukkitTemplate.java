package top.iseason.bukkittemplate;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import top.iseason.bukkittemplate.runtime.RuntimeManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.jar.JarFile;

/**
 * bukkit端主类/入口
 */
public class BukkitTemplate extends JavaPlugin {

    public static RuntimeManager runtimeManager;
    private static JavaPlugin plugin = null;
    private static BukkitPlugin bukkitPlugin = null;

    /**
     * 构造方法，负责下载/添加依赖，并启动插件
     */
    public BukkitTemplate() throws ClassNotFoundException, NoSuchFieldException, RuntimeException {
        plugin = this;
        runtimeManager = PluginYmlRuntime.parsePluginYml();

        bukkitPlugin = loadInstance();
    }

    public static RuntimeManager getRuntimeManager() {
        return runtimeManager;
    }

    /**
     * 获取Bukkit插件主类
     *
     * @return Bukkit插件主类
     */
    public static JavaPlugin getPlugin() {
        return plugin;
    }

    /**
     * 加载插件主类
     */
    private BukkitPlugin loadInstance() {
        Class<?> instanceClass = findInstanceClass();
        if (instanceClass == null) throw new RuntimeException("can not find plugin instance");
        try {
            BukkitPlugin instance;
            try {
                Field instanceField = instanceClass.getDeclaredField("INSTANCE");
                instanceField.setAccessible(true);
                instance = (BukkitPlugin) instanceField.get(null);
            } catch (Throwable e) {
                try {
                    instance = (BukkitPlugin) instanceClass.newInstance();
                } catch (Throwable ex) {
                    throw new RuntimeException("can not find plugin instance");
                }
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 遍历寻找插件入口类 继承 BukkitPlugin
     *
     * @return 插件入口类
     */
    private Class<?> findInstanceClass() {
        Class<?> target = null;
        Class<BukkitPlugin> bukkitPluginClass = BukkitPlugin.class;
        ClassLoader classLoader = BukkitTemplate.class.getClassLoader();
        //猜测名
        String canonicalName = BukkitTemplate.class.getCanonicalName();
        String guessName = canonicalName.replace(".libs.core.BukkitTemplate", "") + "." + getPlugin().getName();
        try {
            target = Class.forName(guessName, false, classLoader);
            if (target != bukkitPluginClass && bukkitPluginClass.isAssignableFrom(target))
                return target;
        } catch (Exception ignored) {
        }
        // 遍历查找
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
            //并行查找速度更快
            target = jarFile.stream()
                    .parallel()
                    .map((it) -> {
                        String urlName = it.getName();
                        if (!urlName.endsWith(".class") || urlName.startsWith("META-INF")) {
                            return null;
                        }
                        try {
                            String className = urlName.replace('/', '.').substring(0, urlName.length() - 6);
                            return Class.forName(className, false, classLoader);
                        } catch (Throwable ignored) {
                            return null;
                        }
                    }).filter(it -> it != null &&
                            it != bukkitPluginClass &&
                            bukkitPluginClass.isAssignableFrom(it))
                    .findAny()
                    .orElse(null);
        } catch (IOException ignored) {
        }
        return target;
    }

    @Override
    public void onLoad() {
        try {
            bukkitPlugin.onLoad();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        try {
            bukkitPlugin.onEnable();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> bukkitPlugin.onAsyncEnable());
    }

    @Override
    public void onDisable() {
        try {
            bukkitPlugin.onDisable();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bukkit.getScheduler().cancelTasks(this);
        HandlerList.unregisterAll(this);
        DisableHook.disableAll();
    }
}
