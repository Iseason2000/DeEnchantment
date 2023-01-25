package top.iseason.bukkit.deenchantment.command

import org.bukkit.entity.Player
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.permissions.PermissionDefault
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantmentWrapper
import top.iseason.bukkit.deenchantment.settings.Message
import top.iseason.bukkit.deenchantment.utils.EnchantTools
import top.iseason.bukkittemplate.command.CommandNode
import top.iseason.bukkittemplate.command.CommandNodeExecutor
import top.iseason.bukkittemplate.command.Param
import top.iseason.bukkittemplate.command.ParmaException
import top.iseason.bukkittemplate.utils.bukkit.ItemUtils.applyMeta
import top.iseason.bukkittemplate.utils.bukkit.ItemUtils.checkAir
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.formatBy
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.noColor
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.sendColorMessage
import top.iseason.bukkittemplate.utils.other.NumberUtils.toRoman

object AddCommand : CommandNode(
    name = "add",
    default = PermissionDefault.OP,
    description = "将特定负魔添加到手上的东西上",
    params = listOf(
        Param("[name]") {
            BaseEnchant.enchants.filter { it.enable }.map { it.translateName.noColor()!! }
        },
        Param("<level>", listOf("1", "2", "3", "4", "5"))
    ),
    isPlayerOnly = true,
    async = true
) {
    override var onExecute: CommandNodeExecutor? = CommandNodeExecutor { params, sender ->
        val player = sender as Player
        val en = params.next<DeEnchantmentWrapper>()
        val level = params.nextOrNull<Int>() ?: 1
        val itemInMainHand = player.inventory.itemInMainHand
        if (itemInMainHand.checkAir()) throw ParmaException(Message.command__add_hand)
        itemInMainHand.applyMeta {
            if (this is EnchantmentStorageMeta) addStoredEnchant(en, level, true)
            else addEnchant(en, level, true)
            EnchantTools.updateLore(this)
        }
        sender.sendColorMessage(Message.command__add.formatBy(en.translateName.noColor() + " " + level.toRoman()))
    }
}
