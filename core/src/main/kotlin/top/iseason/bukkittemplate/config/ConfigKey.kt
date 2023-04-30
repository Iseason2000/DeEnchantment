package top.iseason.bukkittemplate.config

import java.lang.reflect.Field

/**
 * 配置中的一个Key，用于储存反射到的Field，方便修改值
 */
internal class ConfigKey(val key: String, val field: Field, val comments: List<String>?) {
    init {
        field.isAccessible = true
    }

    /**
     *  设置Field中的值
     */
    fun setValue(parent: Any, value: Any) {
        field.set(parent, value)
    }

    /**
     * 读取Field中的值
     */
    fun getValue(parent: Any): Any? {
        return field.get(parent)
    }
}