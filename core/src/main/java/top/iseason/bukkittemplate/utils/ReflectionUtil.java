package top.iseason.bukkittemplate.utils;

import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

// 反射工具
public class ReflectionUtil {
    private static final Unsafe unsafe;
    private static final MethodHandles.Lookup lookup;
    private static final MethodHandle getDeclaredFields0;

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
            //将子依赖填入插件classloader
            Field lookupField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            lookup = (MethodHandles.Lookup) unsafe.getObject(unsafe.staticFieldBase(lookupField), unsafe.staticFieldOffset(lookupField));
            getDeclaredFields0 = lookup.findVirtual(Class.class, "getDeclaredFields0", MethodType.methodType(Field[].class, boolean.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 替换 object 中的某一个变量
     *
     * @param target  被替换的对象
     * @param field   被替换的成员
     * @param value   替换的值
     * @param isForce 是否使用 Unsafe 无视访问权限强制替换
     */
    public static void replaceObject(Object target, Field field, Object value, boolean isForce, boolean isVolatile) throws IllegalAccessException {
        if (isForce) {
            if (isVolatile)
                unsafe.putObjectVolatile(target, unsafe.objectFieldOffset(field), value);
            else
                unsafe.putObject(target, unsafe.objectFieldOffset(field), value);
        } else {
            field.setAccessible(true);
            field.set(target, value);
            field.setAccessible(false);
        }

    }

    /**
     * 强制替换某个类中的成员
     *
     * @param clazz      成员声明的Class
     * @param fieldName  成员名
     * @param target     替换的对象
     * @param value      替换的值
     * @param isVolatile 成员是否是 Volatile 修饰的
     * @throws NoSuchFieldException   找不到该成员
     * @throws IllegalAccessException 替换失败
     */
    public static void replaceFieldValue(
            Class<?> clazz,
            String fieldName,
            Object target,
            Object value,
            boolean isVolatile) throws NoSuchFieldException, IllegalAccessException {
        Field parentField = findField(clazz, fieldName);
        replaceObject(target, parentField, value, true, isVolatile);
    }

    /**
     * <p>强制获取某个类中的某个名字的Field</p>
     * <p>无视访问权限安全模块</p>
     *
     * @param clazz     查找的类
     * @param fieldName 成员名字
     * @return 找到的Field
     * @throws NoSuchFieldException 找不到改名字的Field
     */
    public static Field findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Field field = null;
        try {
            field = clazz.getField(fieldName);
        } catch (NoSuchFieldException e1) {
            try {
                field = clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e2) {
                try {
                    Field[] fields = (Field[]) getDeclaredFields0.invoke(clazz, false);
                    for (Field f : fields) {
                        if (fieldName.equals(f.getName())) {
                            field = f;
                            break;
                        }
                    }
                } catch (Throwable e3) {
                    throw new NoSuchFieldException();
                }
            }
        }
        return field;
    }

}

