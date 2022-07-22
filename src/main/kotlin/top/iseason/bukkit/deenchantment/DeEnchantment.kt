package top.iseason.bukkit.deenchantment

import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import top.iseason.bukkit.bukkittemplate.KotlinPlugin
import top.iseason.bukkit.bukkittemplate.config.ConfigWatcher
import top.iseason.bukkit.bukkittemplate.config.SimpleYAMLConfig
import top.iseason.bukkit.bukkittemplate.debug.SimpleLogger
import top.iseason.bukkit.bukkittemplate.debug.info
import top.iseason.bukkit.deenchantment.manager.ConfigManager

object DeEnchantment : KotlinPlugin() {

    override fun onAsyncLoad() {
//        info("${ChatColor.YELLOW}开始加载插件!")
    }

    override fun onEnable() {
        SimpleYAMLConfig.notifyMessage = "&a配置: &6%s &a已重载!"
        SimpleLogger.prefix =
            "${ChatColor.LIGHT_PURPLE}[${ChatColor.RED}负魔书${ChatColor.LIGHT_PURPLE}]${ChatColor.RESET} "
        ConfigManager.init(javaPlugin)
        Metrics(javaPlugin, 13440)
        info("${ChatColor.AQUA}██████╗ ███████╗███████╗███╗   ██╗ ██████╗██╗  ██╗ █████╗ ███╗   ██╗████████╗")
        info("${ChatColor.AQUA}██╔══██╗██╔════╝██╔════╝████╗  ██║██╔════╝██║  ██║██╔══██╗████╗  ██║╚══██╔══╝")
        info("${ChatColor.AQUA}██║  ██║█████╗  █████╗  ██╔██╗ ██║██║     ███████║███████║██╔██╗ ██║   ██║   ")
        info("${ChatColor.AQUA}██║  ██║██╔══╝  ██╔══╝  ██║╚██╗██║██║     ██╔══██║██╔══██║██║╚██╗██║   ██║   ")
        info("${ChatColor.AQUA}██████╔╝███████╗███████╗██║ ╚████║╚██████╗██║  ██║██║  ██║██║ ╚████║   ██║   ")
        info("${ChatColor.AQUA}╚═════╝ ╚══════╝╚══════╝╚═╝  ╚═══╝ ╚═════╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═══╝   ╚═╝   ")
        info("${ChatColor.GREEN}插件已启用！${ChatColor.DARK_PURPLE}DeEnchantment v${ChatColor.GOLD}${javaPlugin.description.version}")
        Bukkit.getPluginCommand("DeEnchantment")!!.setExecutor(MainCommand())
    }

    override fun onDisable() {
        ConfigManager.quit()
        ConfigWatcher.onDisable()
        info("${ChatColor.GREEN}插件已注销！")
    }

}