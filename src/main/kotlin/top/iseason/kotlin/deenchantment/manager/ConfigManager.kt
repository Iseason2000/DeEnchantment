package top.iseason.kotlin.deenchantment.manager

import org.bukkit.ChatColor
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import top.iseason.kotlin.deenchantment.DeEnchantmentPlugin
import top.iseason.kotlin.deenchantment.utils.DeEnum
import top.iseason.kotlin.deenchantment.utils.LogSender


object ConfigManager {
    private lateinit var plugin: DeEnchantmentPlugin
    private lateinit var config: FileConfiguration
    var deEnchantmentsList = HashSet<DeEnchantmentWrapper>()
    private lateinit var deEnchantmentsNameMap: HashMap<String, DeEnum>
    private lateinit var deEnchantments: HashMap<String, Pair<String, Double>>
    private var isInit = false
    var byKey: Any? = null
    var byName: Any? = null
    private var prefix: String? = null
    private var showLore: Boolean? = null
    fun init(plugin: JavaPlugin) {
        this.plugin = plugin as DeEnchantmentPlugin
        reload()
        isInit = true
    }

    fun reload() {
        if (isInit)
            plugin.reloadConfig()
        config = plugin.config
        val pre = config.getString("Prefix") ?: ""
        prefix = ChatColor.translateAlternateColorCodes('&', pre)
        showLore = config.getBoolean("ShowLore")
        HandlerList.unregisterAll(plugin)
        resetEnchantments()
        deEnchantments = HashMap()
        deEnchantmentsList.clear()
        loadEnchantments()
        DeEnchantment.registerEnchantments()
        ListenerManager.registerListeners()
        loadDeEnchantmentsNameList()
        plugin.saveDefaultConfig()
    }

    fun quit() {
        try {
            resetEnchantments()
        } catch (e: NoClassDefFoundError) {
            LogSender.consoleLog("${ChatColor.RED}注销附魔异常: ${ChatColor.YELLOW}NoClassDefFoundError")
        }
        HandlerList.unregisterAll(plugin)
        deEnchantments = HashMap()
        deEnchantmentsList.clear()
        plugin.saveDefaultConfig()
    }

    private fun loadDeEnchantmentsNameList() {
        deEnchantmentsNameMap = HashMap()
        for (enchantment in deEnchantmentsList) {
            val enchantmentName = getEnchantmentName(enchantment.name)
            deEnchantmentsNameMap[ChatColor.stripColor(enchantmentName)!!] =
                DeEnchantment.getDeEnum(enchantment) ?: continue
        }
    }

    private fun loadEnchantments() {
        val keys = config.getKeys(false)
        val keySets = HashMap<String, Pair<String, Double>>()
        for (key in keys) {
            if (!config.getBoolean("$key.Enable")) continue
            val name = config.getString("$key.DisplayName")
            val colorName = ChatColor.translateAlternateColorCodes('&', name!!)
            val chance = config.getDouble("$key.Chance")
            keySets += Pair(key, Pair(colorName, chance))
        }
        deEnchantments = keySets
    }

    fun getPlugin() = plugin
    fun getConfig() = config
    fun getDeEnchantments() = deEnchantments
    fun getDeEnchantmentsNameMap() = deEnchantmentsNameMap
    fun getEnchantmentName(keyName: String): String? {
        return deEnchantments[keyName]?.first
    }

    fun getPrefix() = prefix
    fun getShowLore() = showLore

    fun getEnchantmentChance(keyName: String): Double? {
        return deEnchantments[keyName]?.second
    }

    private fun resetEnchantments() {
        if (!isInit) return
        val keyField = Enchantment::class.java.getDeclaredField("byKey")
        val nameField = Enchantment::class.java.getDeclaredField("byName")
        keyField.isAccessible = true
        nameField.isAccessible = true
        val keyMap = keyField[null]
        if (keyMap is HashMap<*, *>) {
            var count = 0
            val totalCount = deEnchantmentsList.size
            for (en in deEnchantmentsList) {
                val enchantmentName = getEnchantmentName(en.name)
                count++
                if (!config.getBoolean("CleanConsole")) {
                    LogSender.consoleLog(
                        "${ChatColor.YELLOW}已注销${ChatColor.GRAY}" +
                                "(${ChatColor.GOLD}${count}${ChatColor.GREEN}/${ChatColor.AQUA}$totalCount" +
                                "${ChatColor.GRAY}):$enchantmentName"
                    )
                }
                keyMap.remove(en.key)
            }
            LogSender.consoleLog(
                "${ChatColor.YELLOW}负魔注销完毕"
                        + "(${ChatColor.GOLD}${count}${ChatColor.GREEN}/${ChatColor.AQUA}$totalCount)"
            )
        }
        val nameMap = nameField[null]
        if (nameMap is HashMap<*, *>)
            for (en in deEnchantmentsList) {
                nameMap.remove(en.name)
            }
        keyField.isAccessible = false
        nameField.isAccessible = false
    }
}
