package top.iseason.deenchantment

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import top.iseason.deenchantment.bstats.Metrics
import top.iseason.deenchantment.manager.ConfigManager
import top.iseason.deenchantment.utils.LogSender

class DeEnchantmentPlugin : JavaPlugin() {
    override fun onLoad() {
        LogSender.consoleLog("${ChatColor.YELLOW}开始加载插件!")
    }

    override fun onEnable() {
        ConfigManager.init(this)
        Metrics(this, 13440)
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