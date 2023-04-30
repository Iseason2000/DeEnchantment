package top.iseason.bukkittemplate.config.type

import java.lang.reflect.Field
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 一般List : CopyOnWriteArrayList、LinkedList、ArrayList、Stack
 */
object ListType : ConfigType {
    override fun checkType(clazz: Class<*>): Boolean {
        return List::class.java.isAssignableFrom(clazz)
    }

    override fun read(obj: Any, field: Field): Any {
        val clazz = field.type
        val list = obj as List<*>
        if (CopyOnWriteArrayList::class.java.isAssignableFrom(clazz)) {
            return CopyOnWriteArrayList(list)
        }
        if (LinkedList::class.java.isAssignableFrom(clazz)) {
            return LinkedList(list)
        }
        if (ArrayList::class.java.isAssignableFrom(clazz)) {
            return ArrayList(list)
        }
        if (Stack::class.java.isAssignableFrom(clazz)) {
            return Stack<Any>().apply {
                addAll(list)
            }
        }
        return list
    }

    override fun save(obj: Any): Any {
        return obj
    }

}