package top.iseason.bukkit.deenchantment

import org.bstats.bukkit.Metrics
import org.bukkit.ChatColor
import top.iseason.bukkit.deenchantment.command.*
import top.iseason.bukkit.deenchantment.hooks.EcoEnchantHook
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantmentWrapper
import top.iseason.bukkit.deenchantment.runnables.EquipmentScanner
import top.iseason.bukkit.deenchantment.settings.Config
import top.iseason.bukkit.deenchantment.settings.Message
import top.iseason.bukkittemplate.KotlinPlugin
import top.iseason.bukkittemplate.command.CommandHandler
import top.iseason.bukkittemplate.command.ParamAdopter
import top.iseason.bukkittemplate.config.SimpleYAMLConfig
import top.iseason.bukkittemplate.debug.SimpleLogger
import top.iseason.bukkittemplate.debug.info
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.noColor
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.toColor
import top.iseason.bukkittemplate.utils.other.submit

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
        ParamAdopter(DeEnchantmentWrapper::class, "%s &6不是一个有效的负魔") { str ->
            BaseEnchant.enchants.find { it.translateName.noColor() == str }
        }
        Config.reload()
        setupCommands()
        CommandHandler.updateCommands()
        EcoEnchantHook.checkHooked()
        info("&a插件已启用 作者: &6Iseason &5DeEnchantment v &6${javaPlugin.description.version}")
        submit(period = 10, async = true, task = EquipmentScanner)
    }

    override fun onDisable() {
        EcoEnchantHook.save()
        Config.onDisable()
        info("${ChatColor.GREEN}插件已注销！")
    }

    private fun setupCommands() {
        MainCommand.apply {
            addSubNode(AddCommand)
            addSubNode(GiveCommand)
            addSubNode(RandomCommand)
            addSubNode(ReloadCommand)
            addSubNode(UpdateCommand)
            addSubNode(MigrateCommand)
            addSubNode(PurCommand)
        }
        MainCommand.registerAsRoot()
    }

}