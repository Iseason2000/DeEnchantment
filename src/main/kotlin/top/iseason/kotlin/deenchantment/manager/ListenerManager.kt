package top.iseason.kotlin.deenchantment.manager

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import top.iseason.kotlin.deenchantment.listeners.controllers.*
import top.iseason.kotlin.deenchantment.utils.ClassGetter
import top.iseason.kotlin.deenchantment.utils.LogSender

object ListenerManager {
    val listeners = HashSet<Listener>()
    private val plugin = ConfigManager.getPlugin() as JavaPlugin
    fun registerListeners() {
        registerControllers()
        registerEnchantments()
    }

    private fun registerEnchantments() {
        //自动注册附魔监听器
        val classGetter = ClassGetter(plugin, "top.iseason.kotlin.deenchantment.listeners.enchantments")
        for (cl in classGetter.classes) {
            val constructor = cl.getDeclaredConstructor()
            constructor.isAccessible = true
            val newInstance = constructor.newInstance()
            if (newInstance is Listener && ConfigManager.getConfig()
                    .getBoolean("DE_${cl.simpleName.toUpperCase()}.Enable")
            ) {
                Bukkit.getPluginManager().registerEvents(newInstance, plugin)
                listeners.add(newInstance)
            }
        }
    }

    private fun registerControllers() {
        val config = ConfigManager.getConfig()
        val str = StringBuilder()
        if (config.getBoolean("Anvil")) {
            str.append("铁砧、")
            val anvilListener = AnvilListener()
            Bukkit.getPluginManager().registerEvents(anvilListener, plugin)
            listeners.add(anvilListener)
        }
        if (config.getBoolean("Chest")) {
            str.append("箱子、")
            val chestLootTableListener = ChestLootTableListener()
            Bukkit.getPluginManager().registerEvents(chestLootTableListener, plugin)
            listeners.add(chestLootTableListener)
        }
        if (config.getBoolean("EnchantTable")) {
            str.append("附魔台、")
            val enchantListener = EnchantListener()
            Bukkit.getPluginManager().registerEvents(enchantListener, plugin)
            listeners.add(enchantListener)
        }
        if (config.getBoolean("Mobs")) {
            str.append("怪物、")
            val entitySpawnListener = EntitySpawnListener()
            Bukkit.getPluginManager().registerEvents(entitySpawnListener, plugin)
            listeners.add(entitySpawnListener)
        }
        if (config.getBoolean("Villager")) {
            str.append("村民、")
            val merchantListener = MerchantListener()
            Bukkit.getPluginManager().registerEvents(merchantListener, plugin)
            listeners.add(merchantListener)
        }
        if (config.getBoolean("Fishing")) {
            str.append("钓鱼、")
            val playerFishListener = PlayerFishListener()
            Bukkit.getPluginManager().registerEvents(playerFishListener, plugin)
            listeners.add(playerFishListener)
        }
        if (config.getBoolean("Reward")) {
            str.append("给予、")
            val entityDropItemListener = EntityDropItemListener()
            Bukkit.getPluginManager().registerEvents(entityDropItemListener, plugin)
            listeners.add(entityDropItemListener)
        }
        if (config.getBoolean("Grindstone")) {
            str.append("砂轮、")
            val grindstoneListener = GrindstoneListener()
            Bukkit.getPluginManager().registerEvents(grindstoneListener, plugin)
            listeners.add(grindstoneListener)
        }
        LogSender.consoleLog("${ChatColor.YELLOW}负魔应用于：${ChatColor.WHITE}$str")
    }
}