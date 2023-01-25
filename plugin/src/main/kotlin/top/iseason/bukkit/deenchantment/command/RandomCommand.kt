package top.iseason.bukkit.deenchantment.command

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.permissions.PermissionDefault
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.settings.Message
import top.iseason.bukkit.deenchantment.utils.EnchantTools
import top.iseason.bukkittemplate.command.*
import top.iseason.bukkittemplate.utils.bukkit.EntityUtils.giveItems
import top.iseason.bukkittemplate.utils.bukkit.ItemUtils.applyMeta
import top.iseason.bukkittemplate.utils.bukkit.ItemUtils.checkAir
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.formatBy
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.noColor
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.sendColorMessage
import top.iseason.bukkittemplate.utils.other.NumberUtils.toRoman
import top.iseason.bukkittemplate.utils.other.RandomUtils
import top.iseason.bukkittemplate.utils.other.submit

object RandomCommand : CommandNode(
    name = "random",
    default = PermissionDefault.OP,
    description = "给予玩家随机负魔,不指定等级则随机，不超最大等级",
    params = listOf(
        Param("[type]", suggest = listOf("book", "enchant")),
        Param("[player]", suggestRuntime = ParamSuggestCache.playerParam),
        Param("<level>", listOf("1", "2", "3", "4", "5"))
    ),
    async = true
) {
    override var onExecute: CommandNodeExecutor? = CommandNodeExecutor { params, sender ->
        val type = params.next<String>()
        if (type !in setOf("book", "enchant")) throw ParmaException("&6不支持的类型,应为: book、enchant")
        val player = params.next<Player>()
        if (type == "book") {
            val enchants = BaseEnchant.enchants.filter { wrapper -> wrapper.enable }
            if (enchants.isEmpty()) throw ParmaException("&c没有可用的负魔!")
            val random = enchants.random()
            val level = params.nextOrNull<Int>() ?: if (random.maxLevel == 1) 1
            else RandomUtils.getInteger(1, random.maxLevel)
            val item = ItemStack(Material.ENCHANTED_BOOK).applyMeta {
                val m = this as EnchantmentStorageMeta
                m.addStoredEnchant(random, level, false)
                EnchantTools.updateLore(m)
            }
            submit {
                player.giveItems(item)
            }
            sender.sendColorMessage(
                Message.command__random_book.formatBy(
                    player.name,
                    random.translateName.noColor() + " " + level.toRoman()
                )
            )
        } else {
            val itemInMainHand = player.inventory.itemInMainHand
            if (itemInMainHand.checkAir()) return@CommandNodeExecutor
            val enchants =
                BaseEnchant.enchants.filter { wrapper -> wrapper.enable && wrapper.canEnchantItem(itemInMainHand) }
            if (enchants.isEmpty()) throw ParmaException("&c没有可用的负魔!")
            val random = enchants.random()
            val level = params.nextOrNull<Int>() ?: if (random.maxLevel == 1) 1
            else RandomUtils.getInteger(1, random.maxLevel)
            itemInMainHand.applyMeta {
                if (this is EnchantmentStorageMeta) addStoredEnchant(random, level, true)
                else addEnchant(random, level, true)
                EnchantTools.updateLore(this)
            }
            sender.sendColorMessage(
                Message.command__random_enchant.formatBy(
                    player.name,
                    random.translateName.noColor() + " " + level.toRoman()
                )
            )
        }
    }
}
