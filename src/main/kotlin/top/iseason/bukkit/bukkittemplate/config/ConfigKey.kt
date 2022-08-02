package top.iseason.bukkit.bukkittemplate.config

import java.lang.reflect.Field

class ConfigKey(val key: String, val field: Field, val comments: List<String>?) {

    fun setValue(parent: Any, value: Any) {
        field.isAccessible = true
        field.set(parent, value)
        field.isAccessible = false
    }

    fun getValue(parent: Any): Any? {
        field.isAccessible = true
        val get = field.get(parent)
        field.isAccessible = false
        return get
    }
}