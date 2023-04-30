package top.iseason.bukkittemplate.config.type

import org.bukkit.configuration.MemorySection
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentHashMap

/**
 * 一般Map、ConcurrentHashMap、LinkedHashMap、HashMap
 */
object MapType : ConfigType {

    override fun checkType(clazz: Class<*>): Boolean {
        return Map::class.java.isAssignableFrom(clazz)
    }

    override fun read(obj: Any, field: Field): Any {
        val clazz = field.type
        val values = (obj as MemorySection).getValues(true)
        if (ConcurrentHashMap::class.java.isAssignableFrom(clazz)) {
            return ConcurrentHashMap(values)
        }
        if (LinkedHashMap::class.java.isAssignableFrom(clazz)) {
            return LinkedHashMap(values)
        }
        if (HashMap::class.java.isAssignableFrom(clazz)) {
            return HashMap(values)
        }
        return values
    }

    override fun save(obj: Any): Any {
        return obj
    }
}