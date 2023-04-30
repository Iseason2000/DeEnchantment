package top.iseason.bukkittemplate.runtime.loader;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * 可以直接添加 URL 的classloader
 */
public class AppendableClassLoader extends URLClassLoader {
    public AppendableClassLoader(ClassLoader parent) {
        this(new URL[0], parent);
    }

    public AppendableClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }
}
