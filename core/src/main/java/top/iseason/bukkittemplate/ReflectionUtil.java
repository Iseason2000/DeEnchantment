package top.iseason.bukkittemplate;

import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;

// 反射工具&ClassLoader注入器
public class ReflectionUtil {
    private static final MethodHandle addUrlHandleAssembly;
    private static final Object assemblyUcp;
    private static final Unsafe unsafe;
    private static MethodHandle addUrlHandleIsolated;
    private static LinkedList<URL> urls = new LinkedList<>();
    private static boolean isInit = false;
    private static Object isolatedUcp;

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
            //将子依赖填入插件classloader
            Field ucpField = URLClassLoader.class.getDeclaredField("ucp");
            assemblyUcp = unsafe.getObject(BukkitTemplate.class.getClassLoader(), unsafe.objectFieldOffset(ucpField));
            Field lookupField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            MethodHandles.Lookup lookup = (MethodHandles.Lookup) unsafe.getObject(unsafe.staticFieldBase(lookupField), unsafe.staticFieldOffset(lookupField));
            addUrlHandleAssembly = lookup.findVirtual(assemblyUcp.getClass(), "addURL", MethodType.methodType(void.class, URL.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 初始化反射模块
     */
    public static void init() {
        //通过反射获取ClassLoader addUrl 方法，因为涉及java17 无奈使用UnSafe方法
        if (isInit) return;
        isInit = true;
        urls = null;
        try {
            Field ucpField = URLClassLoader.class.getDeclaredField("ucp");
            isolatedUcp = unsafe.getObject(BukkitTemplate.isolatedClassLoader, unsafe.objectFieldOffset(ucpField));
            Field lookupField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            MethodHandles.Lookup lookup = (MethodHandles.Lookup) unsafe.getObject(unsafe.staticFieldBase(lookupField), unsafe.staticFieldOffset(lookupField));
            addUrlHandleIsolated = lookup.findVirtual(isolatedUcp.getClass(), "addURL", MethodType.methodType(void.class, URL.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将URl添加进插件的ClassLoader
     */
    public static synchronized void addIsolatedURL(URL url) {
        if (url == null) return;
        try {
            if (!isInit)
                urls.add(url);
            else
                addUrlHandleIsolated.invoke(isolatedUcp, url);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将URl添加进插件的ClassLoader
     */
    public static synchronized void addAssemblyURL(URL url) {
        try {
            if (!isInit) return;
            addUrlHandleAssembly.invoke(assemblyUcp, url);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 强行替换 object 中的某一个变量
     *
     * @param target
     * @param field
     * @param value
     */
    public static void replaceObject(Object target, Field field, Object value) throws IllegalAccessException {
        field.setAccessible(true);
        field.set(target, value);
        field.setAccessible(false);
//        unsafe.putObject(target, unsafe.objectFieldOffset(field), value);
    }

    public static URL[] getUrls() {
        return urls.toArray(new URL[0]);
    }

}

