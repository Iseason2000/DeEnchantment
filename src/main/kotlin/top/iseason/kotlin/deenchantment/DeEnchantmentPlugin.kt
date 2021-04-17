package top.iseason.kotlin.deenchantment

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import top.iseason.kotlin.deenchantment.manager.ConfigManager
import top.iseason.kotlin.deenchantment.utils.LogSender

class DeEnchantmentPlugin : JavaPlugin() {
    override fun onLoad() {
        LogSender.log("${ChatColor.YELLOW}开始加载插件!")
    }

    override fun onEnable() {
        ConfigManager.init(this)
        LogSender.log("${ChatColor.AQUA}██████╗ ███████╗███████╗███╗   ██╗ ██████╗██╗  ██╗ █████╗ ███╗   ██╗████████╗")
        LogSender.log("${ChatColor.AQUA}██╔══██╗██╔════╝██╔════╝████╗  ██║██╔════╝██║  ██║██╔══██╗████╗  ██║╚══██╔══╝")
        LogSender.log("${ChatColor.AQUA}██║  ██║█████╗  █████╗  ██╔██╗ ██║██║     ███████║███████║██╔██╗ ██║   ██║   ")
        LogSender.log("${ChatColor.AQUA}██║  ██║██╔══╝  ██╔══╝  ██║╚██╗██║██║     ██╔══██║██╔══██║██║╚██╗██║   ██║   ")
        LogSender.log("${ChatColor.AQUA}██████╔╝███████╗███████╗██║ ╚████║╚██████╗██║  ██║██║  ██║██║ ╚████║   ██║   ")
        LogSender.log("${ChatColor.AQUA}╚═════╝ ╚══════╝╚══════╝╚═╝  ╚═══╝ ╚═════╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═══╝   ╚═╝   ")
        LogSender.log("${ChatColor.GREEN}插件已启用！")
        Bukkit.getPluginCommand("DeEnchantment")!!.setExecutor(Command())
    }
    override fun onDisable() {
        ConfigManager.quit()
        LogSender.log("${ChatColor.GREEN}插件已注销！")
    }

}