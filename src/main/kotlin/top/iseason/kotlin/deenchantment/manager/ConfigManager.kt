package top.iseason.kotlin.deenchantment.manager

import org.bukkit.ChatColor
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.enchantments.Enchantment
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

    //特性设置
    private var isLevelUnlimited: Boolean = false

    fun init(plugin: JavaPlugin) {
        this.plugin = plugin as DeEnchantmentPlugin
        reload()
        isInit = true
    }

    fun reload() {
        if (isInit)
            plugin.reloadConfig()
        config = plugin.config
        resetEnchantments()
        deEnchantments = HashMap()
        deEnchantmentsList.clear()
        loadEnchantments()
        DeEnchantment.registerEnchantments()
        ListenerManager.registerListeners()
        loadDeEnchantmentsNameList()
        plugin.saveDefaultConfig()
        isLevelUnlimited = config.getBoolean("LevelUnlimited")
    }

    fun quit() {
        try {
            resetEnchantments()
        } catch (e: NoClassDefFoundError) {
            LogSender.consoleLog("${ChatColor.RED}注销附魔异常: ${ChatColor.YELLOW}NoClassDefFoundError")
        }
        deEnchantments = HashMap()
        deEnchantmentsList.clear()
        ListenerManager.unRegisterListeners()
        plugin.saveDefaultConfig()
    }

    private fun loadDeEnchantmentsNameList() {
        deEnchantmentsNameMap = HashMap()
        for (enchantment in deEnchantmentsList) {
            val enchantmentName = getEnchantmentName(enchantment.name)
            deEnchantmentsNameMap[ChatColor.stripColor(enchantmentName)!!] = DeEnchantment.getDeEnum(enchantment)
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

    fun isLevelUnlimited() = isLevelUnlimited

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
            var count = 1
            val totalCount = deEnchantmentsList.size
            for (en in deEnchantmentsList) {
                val enchantmentName = getEnchantmentName(en.name)
                LogSender.consoleLog(
                    "${ChatColor.YELLOW}已注销${ChatColor.GRAY}" +
                            "(${ChatColor.GOLD}${count++}${ChatColor.GREEN}/${ChatColor.AQUA}$totalCount" +
                            "${ChatColor.GRAY}):$enchantmentName"
                )
                keyMap.remove(en.key)
            }
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
