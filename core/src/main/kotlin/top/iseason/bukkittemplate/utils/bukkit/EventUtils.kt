package top.iseason.bukkittemplate.utils.bukkit

import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import top.iseason.bukkittemplate.BukkitTemplate
import top.iseason.bukkittemplate.utils.bukkit.EventUtils.FakeEventListener

/**
 * bukkit的事件相关工具
 */
object EventUtils {
    /**
     * 快速注册监听器
     */
    fun Listener.registerListener() {
        Bukkit.getPluginManager().registerEvents(this, BukkitTemplate.getPlugin())
    }

    /**
     * 快速注销监听器
     */
    fun Listener.unregister() {
        HandlerList.unregisterAll(this)
    }

    /**
     * 虚拟的Listener
     */
    fun interface FakeEventListener : EventExecutor, Listener

    /**
     * 通过方法快速注册一个事件监听器
     */
    inline fun <reified T : Event> listen(
        priority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = true,
        noinline action: T.() -> Unit
    ): FakeEventListener {
        return createListener(T::class.java, priority, ignoreCancelled, action)
    }

    /**
     * 根据class创建事件监听器
     */
    @Suppress("UNCHECKED_CAST")
    fun <E : Event> createListener(
        clazz: Class<E>,
        priority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = true,
        action: E.() -> Unit
    ): FakeEventListener {
        val fakeEventListener = FakeEventListener { _, event ->
            runCatching {
                action.invoke(event as E)
            }.getOrElse { it.printStackTrace() }
        }
        Bukkit.getPluginManager()
            .registerEvent(
                clazz, fakeEventListener, priority,
                fakeEventListener, BukkitTemplate.getPlugin(), ignoreCancelled
            )
        return fakeEventListener
    }
}