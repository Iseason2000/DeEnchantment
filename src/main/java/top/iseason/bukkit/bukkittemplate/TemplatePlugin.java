package top.iseason.bukkit.bukkittemplate;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import top.iseason.bukkit.bukkittemplate.dependency.DependencyManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

public class TemplatePlugin extends JavaPlugin {

    private static List<Class<?>> classes;
    private static KotlinPlugin ktPlugin;
    private static TemplatePlugin plugin = null;

    public TemplatePlugin() {
        if (plugin == null) plugin = this;
        //防止卡主线程
        new Thread(() -> {
            DependencyManager.parsePluginYml();
//            DependencyLoader.loadLibs();
            classes = loadClass();
            ktPlugin = findInstance();
            ktPlugin.javaPlugin = this;
            plugin.onAsyncLoad();
            plugin.setEnabled(true);
            Bukkit.getScheduler().runTask(plugin, () -> plugin.onEnabled());
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.onAsyncEnabled());
        }).start();
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
                } catch (NoSuchFieldException | IllegalAccessException ignored) {
                }
            }
        }
        throw new RuntimeException("can not find plugin instance! you need a object class implement KotlinPlugin");
    }

    /**
     * 唤醒所有继承SimpleYAMLConfig 的 object类
     * 目的是为了让插件加载阶段就补全缺失的配置文件
     */
//    private static void callConfigsInstance() {
//        for (Class<?> aClass : classes) {
//            if (SimpleYAMLConfig.class.isAssignableFrom(aClass)) {
//                try {
//                    Field instance = aClass.getDeclaredField("INSTANCE");
//                    instance.setAccessible(true);
//                    instance.get(null);
//                    instance.setAccessible(false);
//                } catch (NoSuchFieldException | IllegalAccessException ignored) {
//                }
//            }
//        }
//    }

    /**
     * 加载需要的class
     *
     * @return 需要的class的集合
     */
    private static List<Class<?>> loadClass() {
        URL location = TemplatePlugin.class.getProtectionDomain().getCodeSource().getLocation();
        ArrayList<Class<?>> classes = new ArrayList<>();
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
                    aClass = Class.forName(name.replace('/', '.').substring(0, name.length() - 6), false, TemplatePlugin.class.getClassLoader());
                } catch (Throwable e) {
                    return;
                }
                if (KotlinPlugin.class.isAssignableFrom(aClass)) {
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
    public static TemplatePlugin getPlugin() {
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

    // 比 onEnabled 先调用
    public void onAsyncLoad() {
//        callConfigsInstance();
        ktPlugin.onAsyncLoad();
    }

    public void onEnabled() {
        ktPlugin.onEnable();
    }

    public void onAsyncEnabled() {
        ktPlugin.onAsyncEnable();
    }

    @Override
    public void onDisable() {
        ktPlugin.onDisable();
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
    }

}
