package top.iseason.bukkit.bukkittemplate.dependency;

import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;

//注入器
public class ClassInjector {
    private static final MethodHandle addUrlHandle;
    private static final Object ucp;

    static {
        //通过反射获取ClassLoader addUrl 方法，因为涉及java17 无奈使用UnSafe方法
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            Unsafe unsafe = (Unsafe) f.get(null);
            Field ucpField = URLClassLoader.class.getDeclaredField("ucp");
            ucp = unsafe.getObject(ClassInjector.class.getClassLoader(), unsafe.objectFieldOffset(ucpField));
            Field lookupField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            MethodHandles.Lookup lookup = (MethodHandles.Lookup) unsafe.getObject(unsafe.staticFieldBase(lookupField), unsafe.staticFieldOffset(lookupField));
            addUrlHandle = lookup.findVirtual(ucp.getClass(), "addURL", MethodType.methodType(void.class, URL.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将URl添加进插件的ClassLoader
     */
    public static void addURL(URL url) {
        try {
            addUrlHandle.invoke(ucp, url);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
