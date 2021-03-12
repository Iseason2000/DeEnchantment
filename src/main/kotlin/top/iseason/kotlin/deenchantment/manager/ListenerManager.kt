package top.iseason.kotlin.deenchantment.manager

import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import top.iseason.kotlin.deenchantment.utils.ClassGetter
import org.bukkit.Bukkit

object ListenerManager {
    fun registerListeners() {
        val plugin = ConfigManager.getPlugin() as JavaPlugin
        val classGetter = ClassGetter(plugin, "top.iseason.kotlin.deenchantment.listeners")
        for (cl in classGetter.classes) {
            val newInstance = cl.newInstance()
            if (newInstance is Listener)
                Bukkit.getPluginManager().registerEvents(newInstance, plugin)
        }
    }
}