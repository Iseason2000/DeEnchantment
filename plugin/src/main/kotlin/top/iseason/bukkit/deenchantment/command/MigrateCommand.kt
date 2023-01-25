package top.iseason.bukkit.deenchantment.command

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.permissions.PermissionDefault
import top.iseason.bukkit.deenchantment.DeEnchantment
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.settings.Config
import top.iseason.bukkit.deenchantment.settings.Message
import top.iseason.bukkittemplate.command.CommandNode
import top.iseason.bukkittemplate.command.CommandNodeExecutor
import top.iseason.bukkittemplate.command.ParmaException
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.sendColorMessage
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.toColor
import java.io.File

object MigrateCommand : CommandNode(
    name = "migrate",
    default = PermissionDefault.OP,
    description = "将v1版本的旧配置迁移到新版",
    async = true,
) {
    override var onExecute: CommandNodeExecutor? = CommandNodeExecutor { params, sender ->
        val file = File(DeEnchantment.javaPlugin.dataFolder, "old_config.yml")
        if (!file.exists()) throw ParmaException("&6请将旧配置文件改名为&a\"old_config.yml\"&6并放入插件配置文件夹中!")
        sender.sendColorMessage(Message.command__migrating)
        val config = YamlConfiguration.loadConfiguration(file)
        Config.anvil = config.getBoolean("Anvil", true)
        Config.chestLoot = config.getBoolean("Chest", true)
        Config.enchant = config.getBoolean("EnchantTable", true)
        Config.spawn = config.getBoolean("Mobs", true)
        Config.trade = config.getBoolean("Villager", true)
        Config.fishing = config.getBoolean("Fishing", true)
        Config.reward = config.getBoolean("Reward", true)
        Config.grindstone = config.getBoolean("Grindstone", true)
        Config.levelUnlimited = config.getBoolean("LevelUnlimited", true)
        Config.tooExpensive = config.getBoolean("AllowTooExpensive", true)
        Config.cleanConsole = config.getBoolean("CleanConsole", true)
        Config.save(false)
        Message.prefix = config.getString("Prefix") ?: Message.prefix
        Message.saveAsync(false)
        for (enchant in BaseEnchant.enchantConfigs) {
            val key = enchant.enchant.key.key.uppercase()
            enchant.enable = config.getBoolean("$key.Enable", true)
            enchant.translate_name = config.getString("$key.DisplayName")?.toColor() ?: enchant.translate_name
            enchant.max_level = config.getInt("$key.MaxLevel", enchant.max_level)
            enchant.chance = config.getDouble("$key.Chance", enchant.chance)
            enchant.save(false)
        }
        sender.sendColorMessage(Message.command__migrate)
    }
}
