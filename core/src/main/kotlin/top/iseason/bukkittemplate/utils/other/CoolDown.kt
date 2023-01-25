@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package top.iseason.bukkittemplate.utils.other

import java.util.*

/**
 * 常规HashMap冷却时间表，适用于大多数情况
 */
class CoolDown<T> {
    private val coolDownMap: HashMap<T, Long> = HashMap()

    /**
     * 检查键值是否在冷却中
     * @param key 键值
     * @param coolDown 冷却时间
     */
    fun check(key: T, coolDown: Long): Boolean {
        return EasyCoolDown.checkType(coolDownMap, key, coolDown)
    }

    fun remove(key: T) = coolDownMap.remove(key)
}

/**
 * 使用弱引用的WeakHashMap冷却时间表，适用于不重要、且时间短的冷却
 */
class WeakCoolDown<T> {
    private val coolDownMap: WeakHashMap<T, Long> = WeakHashMap()

    /**
     * 检查键值是否在冷却中
     * @param key 键值
     * @param coolDown 冷却时间
     * @return true 表示在冷却
     */
    fun check(key: T, coolDown: Long): Boolean {
        return EasyCoolDown.checkType(coolDownMap, key, coolDown)
    }

    fun remove(key: T) = coolDownMap.remove(key)
}

/**
 * 对String键的全局冷却，即开即用，弱引用，适用于不太重要的冷却
 */
object EasyCoolDown {
    private val coolDownMap: WeakHashMap<String, Long> = WeakHashMap()

    /**
     * 检查键值是否在冷却中
     * @param obj 键值
     * @param coolDown 冷却时间
     * @return true 表示在冷却
     */
    fun check(obj: Any, coolDown: Long): Boolean {
        val key = obj.toString()
        return checkType(coolDownMap, key, coolDown)
    }

    // 检查某个map中某个键值是否在某个冷却时间内
    fun <T> checkType(map: MutableMap<T, Long>, obj: T, coolDown: Long): Boolean {
        val lastTime = map[obj]
        val current = System.currentTimeMillis()
        if (lastTime == null) {
            map[obj] = current
            return false
        }
        if (current - lastTime <= coolDown) return true
        map[obj] = current
        return false
    }
}