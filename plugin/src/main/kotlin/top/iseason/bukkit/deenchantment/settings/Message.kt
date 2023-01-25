package top.iseason.bukkit.deenchantment.settings

import org.bukkit.configuration.ConfigurationSection
import top.iseason.bukkittemplate.config.SimpleYAMLConfig
import top.iseason.bukkittemplate.config.annotations.Comment
import top.iseason.bukkittemplate.config.annotations.FilePath
import top.iseason.bukkittemplate.config.annotations.Key
import top.iseason.bukkittemplate.debug.SimpleLogger
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils


@Key
@FilePath("message.yml")
object Message : SimpleYAMLConfig() {
    @Comment(
        "",
        "消息留空将不会显示，使用 '\\n' 可以换行",
        "支持 & 颜色符号，1.17以上支持16进制颜色代码，如 #66ccff",
        "{0}、{1}、{2}、{3} 等格式为该消息独有的变量占位符",
        "所有消息支持PlaceHolderAPI",
        "以下是一些特殊消息, 大小写不敏感，可以通过 \\n 自由组合",
        "以 [BoardCast] 开头将以广播的形式发送，支持BungeeCord",
        "以 [Actionbar] 开头将发送ActionBar消息",
        "以 [Command] 开头将以消息接收者的身份运行命令",
        "以 [Console] 开头将以控制台的身份运行命令",
        "以 [OP-Command] 开头将赋予消息接收者临时op运行命令 (慎用)"
    )
    var readme = ""

    var prefix = "&5[&c负魔书&5]&r "
    var anvil_cost = "&a本次附魔花费:&6 {0}"
    var enchant = "&6你的附魔发生了某些变化!"

    var command__give = "&a已给予&6 {0} &a负魔书 &6{1} "
    var command__random_book = "&a已给予&6 {0} &a负魔书 &6{1} "
    var command__random_enchant = "&a已添加&6 {0} &a负魔 &6{1} "
    var command__add_hand = "&c请拿着有效的物品!"
    var command__add = "&a负魔 &6{0} &a已添加!"
    var command__update_hand = "&c请拿着有效的物品!"
    var command__update = "&alore已更新!"
    var command__reload_success = "&a负魔已重新注册!"
    var command__reload_failure = "&c负魔注册异常，请反馈作者!"

    var command__purification_success = "&a你手中的物品已净化!"

    var command__migrate = "&a迁移完成!"
    var command__migrating = "&6数据迁移中,预计20秒......"

    override fun onLoaded(section: ConfigurationSection) {
        SimpleLogger.prefix = prefix
        MessageUtils.defaultPrefix = prefix
    }
}