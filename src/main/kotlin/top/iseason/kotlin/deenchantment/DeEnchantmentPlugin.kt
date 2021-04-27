package top.iseason.kotlin.deenchantment

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import top.iseason.kotlin.deenchantment.manager.ConfigManager
import top.iseason.kotlin.deenchantment.utils.LogSender

class DeEnchantmentPlugin : JavaPlugin() {
    override fun onLoad() {
        LogSender.consoleLog("${ChatColor.YELLOW}开始加载插件!")
    }

    override fun onEnable() {
        ConfigManager.init(this)
        LogSender.consoleLog("${ChatColor.AQUA}██████╗ ███████╗███████╗███╗   ██╗ ██████╗██╗  ██╗ █████╗ ███╗   ██╗████████╗")
        LogSender.consoleLog("${ChatColor.AQUA}██╔══██╗██╔════╝██╔════╝████╗  ██║██╔════╝██║  ██║██╔══██╗████╗  ██║╚══██╔══╝")
        LogSender.consoleLog("${ChatColor.AQUA}██║  ██║█████╗  █████╗  ██╔██╗ ██║██║     ███████║███████║██╔██╗ ██║   ██║   ")
        LogSender.consoleLog("${ChatColor.AQUA}██║  ██║██╔══╝  ██╔══╝  ██║╚██╗██║██║     ██╔══██║██╔══██║██║╚██╗██║   ██║   ")
        LogSender.consoleLog("${ChatColor.AQUA}██████╔╝███████╗███████╗██║ ╚████║╚██████╗██║  ██║██║  ██║██║ ╚████║   ██║   ")
        LogSender.consoleLog("${ChatColor.AQUA}╚═════╝ ╚══════╝╚══════╝╚═╝  ╚═══╝ ╚═════╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═══╝   ╚═╝   ")
        LogSender.consoleLog("${ChatColor.GREEN}插件已启用！")
        Bukkit.getPluginCommand("DeEnchantment")!!.setExecutor(MainCommand())
    }

    override fun onDisable() {
        ConfigManager.quit()
        LogSender.consoleLog("${ChatColor.GREEN}插件已注销！")
    }

}