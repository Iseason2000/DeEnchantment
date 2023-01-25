package top.iseason.bukkittemplate.command

import org.bukkit.command.CommandSender
import java.util.function.BiConsumer

fun interface CommandNodeExecutor : BiConsumer<Params, CommandSender>