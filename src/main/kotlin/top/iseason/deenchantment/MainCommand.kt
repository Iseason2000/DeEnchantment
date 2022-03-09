package top.iseason.deenchantment

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import top.iseason.deenchantment.manager.ConfigManager
import top.iseason.deenchantment.manager.DeEnchantment
import top.iseason.deenchantment.utils.EnchantTools
import top.iseason.deenchantment.utils.LogSender

class MainCommand : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.isOp) return true
        if (args.isEmpty()) return true
        when (args[0]) {
            "give" -> {
                val level = getLevel(args, sender) ?: return true
                giveEnchantment(sender as Player, args[1], level)
            }
            "add" -> {
                val level = getLevel(args, sender) ?: return true
                addEnchant(sender as Player, args[1], level)
            }
            "reload" -> {
                ConfigManager.reload()
                LogSender.log(sender, "${ChatColor.GREEN}配置已重置!")
                return true
            }

        }
        return true
    }


    private fun getLevel(args: Array<out String>, commandSender: CommandSender): Int? {
        if (args.size == 2) {
            LogSender.log(commandSender, "${ChatColor.RED}请输入负魔等级!")
        }
        if (args.size < 3) return null
        if (commandSender !is Player) return null
        val level: Int
        try {
            level = args[2].toInt()
        } catch (e: NumberFormatException) {
            LogSender.log(commandSender, "${ChatColor.RED}请输入正确的整数!")
            return null
        }
        if (level <= 0) {
            LogSender.log(commandSender, "${ChatColor.RED}请输入大于0的整数!")
            return null
        }
        return level
    }

    private fun giveEnchantment(player: Player, name: String, level: Int) {
        val item = ItemStack(Material.ENCHANTED_BOOK)
        val itemMeta = item.itemMeta!! as EnchantmentStorageMeta
        val nameMap = ConfigManager.getDeEnchantmentsNameMap()
        val deEnum = nameMap[name] ?: return
        val byDeEnum = DeEnchantment.getByDeEnum(deEnum) ?: return
        itemMeta.addStoredEnchant(byDeEnum, level, true)
        EnchantTools.setDeEnchantLore(itemMeta)
        item.itemMeta = itemMeta
        player.inventory.addItem(item)
    }

    private fun addEnchant(player: Player, name: String, level: Int) {
        val itemInMainHand = player.inventory.itemInMainHand
        val itemMeta = itemInMainHand.itemMeta ?: return
        val nameMap = ConfigManager.getDeEnchantmentsNameMap()
        val deEnum = nameMap[name] ?: return
        val byDeEnum = DeEnchantment.getByDeEnum(deEnum) ?: return
        itemMeta.addEnchant(byDeEnum, level, true)
        EnchantTools.setDeEnchantLore(itemMeta)
        itemInMainHand.itemMeta = itemMeta
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String>? {
        if (args.isEmpty()) return null
        val size = args.size
        if (args.size > 2) return null
        val type = args[0]
        if (size == 1 && !(type == "give" || type == "add" || type == "reload")) {
            val list = mutableListOf("give", "add", "reload")
            list.removeIf { s: String -> !s.startsWith(args[0]) }
            return list
        }
        if (size != 2) return null
        if (!(type == "give" || type == "add")) return null
        val nameList = ConfigManager.getDeEnchantmentsNameMap().keys.toMutableList()
        val list: MutableList<String> = nameList
        list.removeIf { s: String -> !s.startsWith(args[1]) }
        return list
    }
}