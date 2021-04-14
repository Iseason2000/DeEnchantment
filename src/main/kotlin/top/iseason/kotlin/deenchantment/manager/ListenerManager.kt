package top.iseason.kotlin.deenchantment.manager

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import top.iseason.kotlin.deenchantment.listeners.controllers.*
import top.iseason.kotlin.deenchantment.utils.ClassGetter
import top.iseason.kotlin.deenchantment.utils.LogSender

object ListenerManager {
    private val plugin = ConfigManager.getPlugin() as JavaPlugin
    fun registerListeners() {
        registerControllers()
        registerEnchantments()
    }

    private fun registerEnchantments() {
        val classGetter = ClassGetter(plugin, "top.iseason.kotlin.deenchantment.listeners.enchantments")
        for (cl in classGetter.classes) {
            val newInstance = cl.newInstance()
            if (newInstance is Listener)
                Bukkit.getPluginManager().registerEvents(newInstance, plugin)
        }
    }

    private fun registerControllers() {
        val config = ConfigManager.getConfig()
        val str = StringBuilder()
        if (config.getBoolean("Anvil")) {
            str.append("铁砧、")
            Bukkit.getPluginManager().registerEvents(AnvilListener(), plugin)
        }
        if (config.getBoolean("Chest")) {
            str.append("箱子、")
            Bukkit.getPluginManager().registerEvents(ChestLootTableListener(), plugin)
        }
        if (config.getBoolean("EnchantTable")) {
            str.append("附魔台、")
            Bukkit.getPluginManager().registerEvents(EnchantListener(), plugin)
        }
        if (config.getBoolean("Mobs")) {
            str.append("怪物、")
            Bukkit.getPluginManager().registerEvents(EntitySpawnListener(), plugin)
        }
        if (config.getBoolean("Villager")) {
            str.append("村民、")
            Bukkit.getPluginManager().registerEvents(MerchantListener(), plugin)
        }
        if (config.getBoolean("Fishing")) {
            str.append("钓鱼、")
            Bukkit.getPluginManager().registerEvents(PlayerFishListener(), plugin)
        }
        LogSender.log("${ChatColor.YELLOW}负魔应用于：${ChatColor.WHITE}$str")
    }
}