package top.iseason.kotlin.deenchantment

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import top.iseason.kotlin.deenchantment.manager.ConfigManager
import top.iseason.kotlin.deenchantment.manager.ListenerManager
import top.iseason.kotlin.deenchantment.utils.LogSender

class DeEnchantmentPlugin : JavaPlugin() {
    override fun onLoad() {
        ConfigManager.init(this)
    }

    override fun onEnable() {
        LogSender.log("${ChatColor.AQUA}██████╗ ███████╗███████╗███╗   ██╗ ██████╗██╗  ██╗ █████╗ ███╗   ██╗████████╗")
        LogSender.log("${ChatColor.AQUA}██╔══██╗██╔════╝██╔════╝████╗  ██║██╔════╝██║  ██║██╔══██╗████╗  ██║╚══██╔══╝")
        LogSender.log("${ChatColor.AQUA}██║  ██║█████╗  █████╗  ██╔██╗ ██║██║     ███████║███████║██╔██╗ ██║   ██║   ")
        LogSender.log("${ChatColor.AQUA}██║  ██║██╔══╝  ██╔══╝  ██║╚██╗██║██║     ██╔══██║██╔══██║██║╚██╗██║   ██║   ")
        LogSender.log("${ChatColor.AQUA}██████╔╝███████╗███████╗██║ ╚████║╚██████╗██║  ██║██║  ██║██║ ╚████║   ██║   ")
        LogSender.log("${ChatColor.AQUA}╚═════╝ ╚══════╝╚══════╝╚═╝  ╚═══╝ ╚═════╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═══╝   ╚═╝   ")
        LogSender.log("${ChatColor.GREEN}插件已启用！")
        ListenerManager.registerListeners()
        Bukkit.getPluginCommand("DeEnchantment")!!.setExecutor(ReloadCommand())

    }
    override fun onDisable() {
        ConfigManager.quit()
        LogSender.log("${ChatColor.GREEN}插件已注销！")
    }

}