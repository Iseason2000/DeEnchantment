package top.iseason.bukkit.deenchantment.hooks

import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import top.iseason.bukkit.bukkittemplate.debug.info
import java.io.File

//TODO:兼容
object EcoEnchantHook {
    var plugin: JavaPlugin? = Bukkit.getPluginManager().getPlugin("EcoEnchants") as JavaPlugin?
    var config: YamlConfiguration? = null

    init {
        if (plugin != null) {
            config = YamlConfiguration.loadConfiguration(File(plugin!!.dataFolder, "lang.yml"))
            info("&a检测到&6 EcoEnchant")
        }
    }


}