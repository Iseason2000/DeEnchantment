package top.iseason.bukkittemplate.runtime;

import top.iseason.bukkittemplate.runtime.loader.AppendableClassLoader;
import top.iseason.bukkittemplate.runtime.loader.IsolatedClassLoader;
import top.iseason.bukkittemplate.utils.ReflectionUtil;

import java.net.URL;

/**
 * 给 ClassLoader 动态添加Url
 */
public class ClassAppender {

    private final AppendableClassLoader assemblyClassLoader;
    private final IsolatedClassLoader isolatedClassLoader;

    /**
     * @param parent 注入依赖的父类, 如果你想给 A 添加运行环境，那么应该传入 A.getParent()
     */
    public ClassAppender(ClassLoader parent) {
        AppendableClassLoader appendableClassLoader = new AppendableClassLoader(parent);
        IsolatedClassLoader isolatedClassLoader = new IsolatedClassLoader(appendableClassLoader);
        this.assemblyClassLoader = appendableClassLoader;
        this.isolatedClassLoader = isolatedClassLoader;
    }

    /**
     * @param assemblyClassLoader 必须是 URLClassLoader 的子类
     * @param isolatedClassLoader
     */
    public ClassAppender(AppendableClassLoader assemblyClassLoader, IsolatedClassLoader isolatedClassLoader) {
        this.assemblyClassLoader = assemblyClassLoader;
        this.isolatedClassLoader = isolatedClassLoader;
    }

    /**
     * 将URl添加进插件的ClassLoader
     */
    public synchronized void addIsolatedURL(URL url) {
        if (url == null) return;
        isolatedClassLoader.addURL(url);
    }

    /**
     * 将URl添加进插件的ClassLoader
     */
    public synchronized void addAssemblyURL(URL url) {
        if (url == null) return;
        assemblyClassLoader.addURL(url);
    }

    /**
     * 将运行环境注入至某个ClassLoader下
     *
     * @param classLoader 目标ClassLoader
     */
    public void appendTo(ClassLoader classLoader) throws NoSuchFieldException, IllegalAccessException {
        ReflectionUtil.replaceFieldValue(ClassLoader.class, "parent", classLoader, isolatedClassLoader, true);
    }

    public AppendableClassLoader getAssemblyClassLoader() {
        return assemblyClassLoader;
    }

    public IsolatedClassLoader getIsolatedClassLoader() {
        return isolatedClassLoader;
    }
}
