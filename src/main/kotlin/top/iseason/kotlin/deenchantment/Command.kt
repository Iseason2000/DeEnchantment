package top.iseason.kotlin.deenchantment

import io.github.bananapuncher714.nbteditor.NBTEditor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import top.iseason.kotlin.deenchantment.manager.DeEnchantment
import top.iseason.kotlin.deenchantment.utils.EnchantTools.setDeEnchantLore


class Command : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return true
        val item = ItemStack(Material.ENCHANTED_BOOK)
        val itemMeta = item.itemMeta!! as EnchantmentStorageMeta
//        itemMeta.addStoredEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10, true)
//        itemMeta.addStoredEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5, true)
        itemMeta.addStoredEnchant(DeEnchantment.DE_protection, 5, true)
        itemMeta.addStoredEnchant(DeEnchantment.DE_protection, 10, true)
        itemMeta.addStoredEnchant(DeEnchantment.DE_fire_aspect, 3, true)
        itemMeta.addStoredEnchant(DeEnchantment.DE_impaling, 3, true)
        itemMeta.addStoredEnchant(DeEnchantment.DE_sweeping, 3, true)
        setDeEnchantLore(itemMeta)
        item.itemMeta = itemMeta
        sender.inventory.addItem(item)
        println(NBTEditor.getNBTCompound(sender.inventory.itemInMainHand).toJson())
        return true
    }
}


