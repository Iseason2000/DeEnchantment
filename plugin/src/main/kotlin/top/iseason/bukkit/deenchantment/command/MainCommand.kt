package top.iseason.bukkit.deenchantment.command

import org.bukkit.permissions.PermissionDefault
import top.iseason.bukkittemplate.command.CommandNode

object MainCommand : CommandNode(
    name = "deenchantment",
    alias = arrayOf("de", "den"),
    default = PermissionDefault.OP,
    description = "负魔根命令"
)
