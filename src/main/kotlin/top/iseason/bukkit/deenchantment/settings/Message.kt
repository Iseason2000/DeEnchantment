package top.iseason.bukkit.deenchantment.settings

import org.bukkit.configuration.file.FileConfiguration
import top.iseason.bukkit.bukkittemplate.config.SimpleYAMLConfig
import top.iseason.bukkit.bukkittemplate.config.annotations.FilePath
import top.iseason.bukkit.bukkittemplate.config.annotations.Key
import top.iseason.bukkit.bukkittemplate.debug.SimpleLogger
import top.iseason.bukkit.bukkittemplate.utils.MessageUtils
import top.iseason.bukkit.bukkittemplate.utils.toColor


@Key
@FilePath("message.yml")
object Message : SimpleYAMLConfig() {
    var prefix = "&5[&c负魔书&5]&r "
    var anvil_cost = "&a本次附魔花费:&6 {0}"
    var enchant = "&6你的附魔发生了某些变化!"

    var command__give = "&a已给予&6 {0} &a负魔书 &6{1} "
    var command__add_hand = "&c请拿着有效的物品!"
    var command__add = "&a负魔 &6{0} &a已添加!"
    var command__update_hand = "&c请拿着有效的物品!"
    var command__update = "&alore已更新!"
    var command__reload_success = "&a负魔已重新注册!"
    var command__reload_failure = "&c负魔注册异常，请反馈作者!"

    override val onLoaded: (FileConfiguration.() -> Unit) = {
        SimpleLogger.prefix = prefix.toColor()
        MessageUtils.defaultPrefix = SimpleLogger.prefix
    }
}