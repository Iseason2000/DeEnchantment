package top.iseason.bukkittemplate.runtime.loader;

import javax.management.loading.MLet;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>插件自定义的加载器，用于隔离依赖</p>
 * <p>本ClassLoader将优先加载urls中有的class，而不是双亲委托</p>
 */
public class IsolatedClassLoader extends MLet {
    /**
     * 自由添加黑名单
     */
    public static final Set<String> BLACK_LIST = new HashSet<>();

    public IsolatedClassLoader(ClassLoader parent) {
        this(new URL[0], parent);
    }

    public IsolatedClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public IsolatedClassLoader() {
        super();
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

    public static void addBlackList(Class<?> clazz) {
        BLACK_LIST.add(clazz.getName());
        List<String> subClasses = Arrays.stream(clazz.getDeclaredClasses()).map(Class::getName).collect(Collectors.toList());
        BLACK_LIST.addAll(subClasses);
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }
}