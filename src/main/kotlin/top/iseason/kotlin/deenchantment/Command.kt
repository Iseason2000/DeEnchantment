package top.iseason.kotlin.deenchantment

import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import top.iseason.kotlin.deenchantment.utils.EnchantTools.setDeEnchantLore


class Command : CommandExecutor  {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return true
        var item = ItemStack(Material.DIAMOND_SWORD)
        val itemMeta = item.itemMeta!!
        itemMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,10,true)
        itemMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,5,true)
        itemMeta.addEnchant(DeEnchantment.DE_protection,5,true)
        itemMeta.addEnchant(Enchantment.PROTECTION_EXPLOSIONS,10,true)
        setDeEnchantLore(itemMeta)
        item.itemMeta =  itemMeta
        sender.inventory.addItem(item)
        return true
    }
}


