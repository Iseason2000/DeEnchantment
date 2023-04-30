package top.iseason.bukkittemplate.config.type

import java.lang.reflect.Field
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

/**
 * 一般Set CopyOnWriteArraySet、ConcurrentHashMap.KeySetView、LinkedHashSet、HashSet、
 */
object SetType : ConfigType {
    override fun checkType(clazz: Class<*>): Boolean {
        return Set::class.java.isAssignableFrom(clazz)
    }

    override fun read(obj: Any, field: Field): Any {
        val clazz = field.type
        val set = obj as Collection<*>
        if (CopyOnWriteArraySet::class.java.isAssignableFrom(clazz)) {
            return CopyOnWriteArraySet(set)
        }
        if (ConcurrentHashMap.KeySetView::class.java.isAssignableFrom(clazz)) {
            val newKeySet = ConcurrentHashMap.newKeySet<Any>(set.size)
            newKeySet.addAll(set)
            return newKeySet
        }
        if (LinkedHashSet::class.java.isAssignableFrom(clazz)) {
            return LinkedHashSet(set)
        }
        if (HashSet::class.java.isAssignableFrom(clazz)) {
            return HashSet(set)
        }
        return set.toHashSet()
    }

    override fun save(obj: Any): Any {
        return (obj as Collection<*>).toList()
    }


}