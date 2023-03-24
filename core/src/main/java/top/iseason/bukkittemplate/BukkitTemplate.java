package top.iseason.bukkittemplate;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import top.iseason.bukkittemplate.dependency.PluginDependency;
import top.iseason.bukkittemplate.loader.IsolatedClassLoader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarFile;

/**
 * bukkit端主类/入口
 */
public class BukkitTemplate extends JavaPlugin {

    public static ClassLoader isolatedClassLoader;
    private static JavaPlugin plugin = null;
    private static Object bootStrap = null;

    /**
     * 构造方法，负责下载/添加依赖，并启动插件
     */
    public BukkitTemplate() throws ClassNotFoundException {
        plugin = this;
        if (!PluginDependency.parsePluginYml()) {
            throw new RuntimeException("Loading dependencies error! please check your network!");
        }
        Bukkit.getLogger().info("[" + BukkitTemplate.getPlugin().getName() + "] Loading libraries successfully");
        ReflectionUtil.addIsolatedURL(BukkitTemplate.class.getProtectionDomain().getCodeSource().getLocation());
        isolatedClassLoader = new IsolatedClassLoader(
                ReflectionUtil.getUrls(),
                BukkitTemplate.class.getClassLoader()
        );
        ReflectionUtil.init();
        loadInstance();
    }

    /**
     * 加载插件主类
     */
    private static void loadInstance() {
        Class<?> instanceClass = findInstanceClass();
        if (instanceClass == null) throw new RuntimeException("can not find plugin instance");
        try {
            Class<?> aClass = isolatedClassLoader.loadClass(PluginBootStrap.class.getName());
            Constructor<?> constructor = aClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            bootStrap = constructor.newInstance();
            constructor.setAccessible(false);
            Field instance = instanceClass.getDeclaredField("INSTANCE");
            instance.setAccessible(true);
            for (Field declaredField : bootStrap.getClass().getDeclaredFields()) {
                String name = declaredField.getType().getName();
                if (name.equals(JavaPlugin.class.getName())) {
                    ReflectionUtil.replaceObject(bootStrap, declaredField, plugin);
                } else if (name.equals(BukkitPlugin.class.getName())) {
                    ReflectionUtil.replaceObject(bootStrap, declaredField, instance.get(null));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 遍历寻找插件入口类 继承 BukkitPlugin
     *
     * @return 插件入口类
     */
    private static Class<?> findInstanceClass() {
        Class<?> target;
        String name = BukkitPlugin.class.getName();
        //猜测名
        String canonicalName = BukkitTemplate.class.getCanonicalName();
        String guessName = canonicalName.replace(".libs.core.BukkitTemplate", "") + "." + getPlugin().getName();
        try {
            target = Class.forName(guessName, false, isolatedClassLoader);
            Class<?>[] interfaces = target.getInterfaces();
            for (Class<?> anInterface : interfaces) {
                if (anInterface.getName().equals(name)) {
                    return target;
                }
            }
        } catch (Exception ignored) {
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
        AtomicReference<Class<?>> clazz = new AtomicReference<>(null);
        AtomicBoolean find = new AtomicBoolean(false);
        try (JarFile jarFile = new JarFile(srcFile)) {
            //并行查找速度更快
            jarFile.stream().parallel().forEach((it) -> {
                if (find.get()) return;
                String urlName = it.getName();
                if (!urlName.endsWith(".class") || urlName.startsWith("META-INF")) {
                    return;
                }
                Class<?> aClass;
                try {
                    String className = urlName.replace('/', '.').substring(0, urlName.length() - 6);
                    aClass = Class.forName(className, false, BukkitTemplate.class.getClassLoader());
                    Class<?>[] interfaces = aClass.getInterfaces();
                    if (interfaces.length == 0) return;
                    for (Class<?> anInterface : interfaces) {
                        if (name.equals(anInterface.getName())) {
                            find.set(true);
                            clazz.set(Class.forName(className, false, isolatedClassLoader));
                        }
                    }
                } catch (Throwable ignored) {
                }
            });
        } catch (IOException ignored) {
        }
        return clazz.get();
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
     * 为了支持混淆，所以不会出现方法名
     * 故而通过特定参数来识别方法
     */
    private static <T> void invokeBootStrapMethod(Class<T> sign)
            throws InvocationTargetException, IllegalAccessException {
        Method target = null;
        for (Method declaredMethod : bootStrap.getClass().getDeclaredMethods()) {
            Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
            if (parameterTypes.length == 1 && parameterTypes[0] == sign) {
                target = declaredMethod;
                break;
            }
        }
        if (target != null) {
            target.setAccessible(true);
            target.invoke(bootStrap, (Object) null);
            target.setAccessible(false);
        }
    }

    @Override
    public void onLoad() {
        try {
            invokeBootStrapMethod(Float.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        try {
            invokeBootStrapMethod(Boolean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        try {
            invokeBootStrapMethod(Double.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
