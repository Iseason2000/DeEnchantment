package top.iseason.bukkit.deenchantment

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.permissions.PermissionDefault
import top.iseason.bukkit.bukkittemplate.command.Param
import top.iseason.bukkit.bukkittemplate.command.ParmaException
import top.iseason.bukkit.bukkittemplate.command.commandRoot
import top.iseason.bukkit.bukkittemplate.utils.bukkit.applyMeta
import top.iseason.bukkit.bukkittemplate.utils.bukkit.checkAir
import top.iseason.bukkit.bukkittemplate.utils.bukkit.giveItems
import top.iseason.bukkit.bukkittemplate.utils.noColor
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantmentWrapper
import top.iseason.bukkit.deenchantment.settings.Config
import top.iseason.bukkit.deenchantment.utils.EnchantTools

fun mainCommand() {
    commandRoot(
        "deenchantment", alias = arrayOf("de", "den"), default = PermissionDefault.OP, description = "负魔根命令"
    ) {
        node(
            "give",
            default = PermissionDefault.OP,
            description = "给予自己特定负魔书",
            params = arrayOf(Param("[name]") {
                BaseEnchant.enchants.filter { it.enable }.map { it.translateName.noColor()!! }
            }, Param("<level>", listOf("1", "2", "3"))),
            isPlayerOnly = true, async = true
        ) {
            onExecute {
                val player = it as Player
                val en = getParam<DeEnchantmentWrapper>(0)
                val level = getOptionalParam<Int>(1) ?: 1
                val item = ItemStack(Material.ENCHANTED_BOOK).applyMeta {
                    val m = this as EnchantmentStorageMeta
                    m.addStoredEnchant(en, level, false)
                    EnchantTools.updateLore(m)
                }
                player.giveItems(item)
                onSuccess("&a负魔书 &6${en.translateName.noColor()} &a已获得!")
                true
            }

        }
        node(
            "add",
            default = PermissionDefault.OP,
            description = "将特定负魔添加到手上的东西上",
            params = arrayOf(Param("[name]") {
                BaseEnchant.enchants.filter { it.enable }.map { it.translateName.noColor()!! }
            }, Param("<level>", listOf("1", "2", "3"))),
            isPlayerOnly = true, async = true
        ) {
            onExecute {
                val player = it as Player
                val en = getParam<DeEnchantmentWrapper>(0)
                val level = getOptionalParam<Int>(1) ?: 1
                val itemInMainHand = player.inventory.itemInMainHand
                if (itemInMainHand.type.checkAir()) throw ParmaException("&c请拿着有效的物品!")
                itemInMainHand.applyMeta {
                    if (this is EnchantmentStorageMeta) addStoredEnchant(en, level, true)
                    else addEnchant(en, level, true)
                    EnchantTools.updateLore(this)
                }
                true
            }
        }
        node(
            "reload",
            default = PermissionDefault.OP,
            description = "重新注册负魔", async = true
        ) {
            onExecute {
                try {
                    Config.reload()
                    true
                } catch (e: Exception) {
                    false
                }
            }
            onSuccess("&a负魔已重新注册!")
            onFailure("负魔注册异常，请反馈作者!")
        }
    }
}
