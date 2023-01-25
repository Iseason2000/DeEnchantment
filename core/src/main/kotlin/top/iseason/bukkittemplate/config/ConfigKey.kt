package top.iseason.bukkittemplate.config

import java.lang.reflect.Field

/**
 * 配置中的一个Key，用于储存反射到的Field，方便修改值
 */
class ConfigKey(val key: String, val field: Field, val comments: List<String>?) {

    /**
     *  设置Field中的值
     */
    fun setValue(parent: Any, value: Any) {
        field.isAccessible = true
        field.set(parent, value)
        field.isAccessible = false
    }

    /**
     * 读取Field中的值
     */
    fun getValue(parent: Any): Any? {
        field.isAccessible = true
        val get = field.get(parent)
        field.isAccessible = false
        return get
    }
}