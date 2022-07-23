package top.iseason.bukkit.deenchantment.settings

import org.bukkit.configuration.file.FileConfiguration
import top.iseason.bukkit.bukkittemplate.config.SimpleYAMLConfig
import top.iseason.bukkit.bukkittemplate.config.annotations.FilePath
import top.iseason.bukkit.bukkittemplate.config.annotations.Key
import top.iseason.bukkit.bukkittemplate.debug.SimpleLogger
import top.iseason.bukkit.bukkittemplate.utils.toColor


@Key
@FilePath("message.yml")
object Message : SimpleYAMLConfig() {
    var prefix = "&5[&c负魔书&5]&r "

    override val onLoaded: (FileConfiguration.() -> Unit) = {
        SimpleLogger.prefix = prefix.toColor()
    }
}