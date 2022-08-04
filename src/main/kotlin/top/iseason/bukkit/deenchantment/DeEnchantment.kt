package top.iseason.bukkit.deenchantment

import org.bstats.bukkit.Metrics
import org.bukkit.ChatColor
import top.iseason.bukkit.bukkittemplate.KotlinPlugin
import top.iseason.bukkit.bukkittemplate.command.CommandBuilder
import top.iseason.bukkit.bukkittemplate.command.TypeParam
import top.iseason.bukkit.bukkittemplate.config.ConfigWatcher
import top.iseason.bukkit.bukkittemplate.config.SimpleYAMLConfig
import top.iseason.bukkit.bukkittemplate.debug.SimpleLogger
import top.iseason.bukkit.bukkittemplate.debug.info
import top.iseason.bukkit.bukkittemplate.utils.noColor
import top.iseason.bukkit.bukkittemplate.utils.submit
import top.iseason.bukkit.bukkittemplate.utils.toColor
import top.iseason.bukkit.deenchantment.command.mainCommand
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantmentWrapper
import top.iseason.bukkit.deenchantment.runnables.EquipmentScanner
import top.iseason.bukkit.deenchantment.settings.Config
import top.iseason.bukkit.deenchantment.settings.Message

object DeEnchantment : KotlinPlugin() {

    override fun onEnable() {
        SimpleYAMLConfig.notifyMessage = "&a配置: &6%s &a已重载!"
        Metrics(javaPlugin, 13440)
    }

    override fun onAsyncEnable() {
        Message.load(false)
        SimpleLogger.prefix = Message.prefix.toColor()
        info("${ChatColor.AQUA}██████╗ ███████╗███████╗███╗   ██╗ ██████╗██╗  ██╗ █████╗ ███╗   ██╗████████╗")
        info("${ChatColor.AQUA}██╔══██╗██╔════╝██╔════╝████╗  ██║██╔════╝██║  ██║██╔══██╗████╗  ██║╚══██╔══╝")
        info("${ChatColor.AQUA}██║  ██║█████╗  █████╗  ██╔██╗ ██║██║     ███████║███████║██╔██╗ ██║   ██║   ")
        info("${ChatColor.AQUA}██║  ██║██╔══╝  ██╔══╝  ██║╚██╗██║██║     ██╔══██║██╔══██║██║╚██╗██║   ██║   ")
        info("${ChatColor.AQUA}██████╔╝███████╗███████╗██║ ╚████║╚██████╗██║  ██║██║  ██║██║ ╚████║   ██║   ")
        info("${ChatColor.AQUA}╚═════╝ ╚══════╝╚══════╝╚═╝  ╚═══╝ ╚═════╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═══╝   ╚═╝   ")
        TypeParam(DeEnchantmentWrapper::class, { "$it &6不是一个有效的负魔" }) { str ->
            BaseEnchant.enchants.find { it.translateName.noColor() == str }
        }
        Config.reload()
        mainCommand()
        CommandBuilder.onEnable()
        info("&a插件已启用！作者: &6Iseason &5DeEnchantments v &6${javaPlugin.description.version}")
        submit(period = 10, async = true, task = EquipmentScanner)
    }

    override fun onDisable() {
        Config.onDisable()
        ConfigWatcher.onDisable()
        CommandBuilder.onDisable()
        info("${ChatColor.GREEN}插件已注销！")
    }

}