package top.iseason.bukkittemplate.loader;

import top.iseason.bukkittemplate.BukkitTemplate;
import top.iseason.bukkittemplate.ReflectionUtil;
import top.iseason.bukkittemplate.dependency.DependencyDownloader;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

/**
 * 插件自定义的加载器，用于隔离依赖
 */
public class IsolatedClassLoader extends URLClassLoader {

    public static final Set<String> BLACK_LIST = new HashSet<String>() {{
        add(BukkitTemplate.class.getName());
        add(IsolatedClassLoader.class.getName());
        add(DependencyDownloader.class.getName());
        add(ReflectionUtil.class.getName());
    }};

    public IsolatedClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> loadedClass = findLoadedClass(name);
            if (loadedClass == null) {
                // 读取依赖
                if (!BLACK_LIST.contains(name)) {
                    try {
                        loadedClass = findClass(name);
                    } catch (ClassNotFoundException ignored) {
                    }
                }
                // 不是依赖
                if (loadedClass == null) {
                    ClassLoader parent = getParent();
                    if (parent != null) {
                        loadedClass = parent.loadClass(name);
                    }
                }
            }
            if (resolve) {
                resolveClass(loadedClass);
            }
            return loadedClass;
        }
    }
}