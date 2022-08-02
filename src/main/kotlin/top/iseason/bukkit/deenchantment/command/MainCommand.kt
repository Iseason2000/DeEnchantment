package top.iseason.bukkit.deenchantment.command

import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.permissions.PermissionDefault
import top.iseason.bukkit.bukkittemplate.command.Param
import top.iseason.bukkit.bukkittemplate.command.ParamSuggestCache
import top.iseason.bukkit.bukkittemplate.command.ParmaException
import top.iseason.bukkit.bukkittemplate.command.commandRoot
import top.iseason.bukkit.bukkittemplate.utils.*
import top.iseason.bukkit.bukkittemplate.utils.bukkit.applyMeta
import top.iseason.bukkit.bukkittemplate.utils.bukkit.checkAir
import top.iseason.bukkit.bukkittemplate.utils.bukkit.giveItems
import top.iseason.bukkit.deenchantment.DeEnchantment
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.manager.DeEnchantmentWrapper
import top.iseason.bukkit.deenchantment.settings.Config
import top.iseason.bukkit.deenchantment.settings.Message
import top.iseason.bukkit.deenchantment.utils.EnchantTools
import java.io.File

fun mainCommand() {
    commandRoot(
        "deenchantment", alias = arrayOf("de", "den"), default = PermissionDefault.OP, description = "负魔根命令"
    ) {
        node(
            "give",
            default = PermissionDefault.OP,
            description = "给予玩家特定负魔书",
            params = arrayOf(Param("[player]", suggestRuntime = ParamSuggestCache.playerParam), Param("[name]") {
                BaseEnchant.enchants.filter { it.enable }.map { it.translateName.noColor()!! }
            }, Param("<level>", listOf("1", "2", "3"))),
            async = true
        ) {
            onExecute {
                val player = getParam<Player>(0)
                val en = getParam<DeEnchantmentWrapper>(1)
                val level = getOptionalParam<Int>(2) ?: 1
                val item = ItemStack(Material.ENCHANTED_BOOK).applyMeta {
                    val m = this as EnchantmentStorageMeta
                    m.addStoredEnchant(en, level, false)
                    EnchantTools.updateLore(m)
                }
                player.giveItems(item)
                onSuccess(
                    Message.command__give.formatBy(
                        player.name,
                        en.translateName.noColor() + " " + level.toRoman()
                    )
                )
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
                if (itemInMainHand.type.checkAir()) throw ParmaException(Message.command__add_hand)
                itemInMainHand.applyMeta {
                    if (this is EnchantmentStorageMeta) addStoredEnchant(en, level, true)
                    else addEnchant(en, level, true)
                    EnchantTools.updateLore(this)
                }
                onSuccess(Message.command__add.formatBy(en.translateName.noColor() + " " + level.toRoman()))
                true
            }
        }
        node(
            "update",
            default = PermissionDefault.OP,
            description = "更新手上装备的负魔描述及名称",
            isPlayerOnly = true, async = true
        ) {
            onExecute {
                val player = it as Player
                val itemInMainHand = player.inventory.itemInMainHand
                if (itemInMainHand.type.checkAir()) {
                    throw ParmaException(Message.command__update_hand)
                }
                itemInMainHand.applyMeta {
                    EnchantTools.updateLore(this)
                }
                true
            }
            onSuccess(Message.command__update)
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
            onSuccess(Message.command__reload_success)
            onFailure(Message.command__reload_failure)
        }
        //迁移
        node(
            "migrate",
            default = PermissionDefault.OP,
            description = "将v1版本的旧配置迁移到新版", async = true
        ) {
            onExecute {
                val file = File(DeEnchantment.javaPlugin.dataFolder, "old_config.yml")
                if (!file.exists()) throw ParmaException("&6请将旧配置文件改名为&a\"old_config.yml\"&6并放入插件配置文件夹中!")
                it.sendColorMessage(Message.command__migrating)
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
                true
            }
            onSuccess(Message.command__migrate)
        }
    }
}
