package top.iseason.bukkit.bukkittemplate.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginCommand
import org.bukkit.command.SimpleCommandMap
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.SimplePluginManager
import top.iseason.bukkit.bukkittemplate.TemplatePlugin
import top.iseason.bukkit.bukkittemplate.utils.toColor
import java.lang.reflect.Constructor
import java.util.*
import java.util.stream.Collectors

@Suppress("unused")
class CommandBuilder(private val commandNode: CommandNode) {

    /**
     * 节点执行代码
     */
    fun onExecute(onExecute: Params.(sender: CommandSender) -> Boolean): CommandBuilder {
        commandNode.onExecute = onExecute
        return this
    }

    /**
     * 执行失败的提示信息
     */
    fun onFailure(onFailure: String): CommandBuilder {
        commandNode.failureMessage = onFailure
        return this
    }

    /**
     * 执行成功的提示信息
     */
    fun onSuccess(onSuccess: String): CommandBuilder {
        commandNode.successMessage = onSuccess
        return this
    }

    /**
     * 缺少权限的提示信息
     */
    fun onNoPermissions(onNoPermissions: String): CommandBuilder {
        commandNode.noPermissionMessage = onNoPermissions
        return this
    }

    /**
     * 添加节点
     */
    fun node(commandNode: CommandNode): CommandBuilder {
        this.commandNode.addSubNode(commandNode)
        pluginPermissions.add(commandNode.permission)
        return CommandBuilder(commandNode)
    }

    /**
     * 添加节点
     */
    fun node(
        /**
         * 节点名称
         */
        name: String,
        /**
         * 节点别名
         */
        alias: Array<String>? = null,
        /**
         * 默认权限
         */
        default: PermissionDefault = PermissionDefault.TRUE,
        /**
         * 描述
         */
        description: String? = null,
        /**
         * 是否异步运行
         */
        async: Boolean = false,
        /**
         * 指定参数
         */
        params: Array<Param> = emptyArray(),
        /**
         * 是否只允许玩家
         */
        isPlayerOnly: Boolean = false,
        /**
         * 命令执行
         */
        onScope: (CommandBuilder.() -> Unit)? = null
    ): CommandBuilder {
        val commandNode = CommandNode(name, alias, description, default, async, params, isPlayerOnly)
        this.commandNode.addSubNode(commandNode)
        pluginPermissions.add(commandNode.permission)
        val commandBuilder = CommandBuilder(commandNode)
        onScope?.invoke(commandBuilder)
        return commandBuilder
    }

    companion object {
        private val pluginPermissions = mutableSetOf<Permission>()
        private val registeredCommands = mutableListOf<PluginCommand>()
        private val simpleCommandMap: SimpleCommandMap
        private val pluginCommandConstructor: Constructor<PluginCommand> = getPluginCommandConstructor()

        init {
            val simplePluginManager = Bukkit.getServer().pluginManager as SimplePluginManager
            val commandMapField = SimplePluginManager::class.java.getDeclaredField("commandMap")
            commandMapField.isAccessible = true
            simpleCommandMap = commandMapField.get(simplePluginManager) as SimpleCommandMap
        }

        private fun getPluginCommandConstructor(): Constructor<PluginCommand> {
            val constructor = PluginCommand::class.java.getDeclaredConstructor(String::class.java, Plugin::class.java)
            constructor.isAccessible = true
            return constructor
        }

        // 注册根命令
        fun register(commandNode: CommandNode) {
            require(commandNode.parent == null) { "只能为根命令注册!" }
            val pluginCommand =
                pluginCommandConstructor.newInstance(commandNode.name, TemplatePlugin.getPlugin()) as PluginCommand
            if (commandNode.alias != null) {
                pluginCommand.aliases = Arrays.stream(commandNode.alias).collect(Collectors.toList())
            }
            if (commandNode.description != null) {
                pluginCommand.description = commandNode.description
            }
            pluginCommand.permissionMessage = commandNode.noPermissionMessage?.toColor()
            pluginCommand.setExecutor(commandNode)
            pluginCommand.tabCompleter = commandNode
            simpleCommandMap.register(TemplatePlugin.getPlugin().name, pluginCommand)
            pluginPermissions.add(commandNode.permission)
            registeredCommands.add(pluginCommand)
        }

        // 注销根命令
        fun unregister(commandNode: CommandNode) {
            require(commandNode.parent == null) { "非根命令不需要注销!" }
            Bukkit.getServer().getPluginCommand(commandNode.name)?.unregister(simpleCommandMap)
        }

        @JvmStatic
        fun unregisterAll() {
            for (registeredCommand in registeredCommands) {
                registeredCommand.unregister(simpleCommandMap)
            }
        }

        fun addPermissions(perm: Permission) {
            pluginPermissions.add(perm)
        }

        fun clearPermissions() {
            for (pluginPermission in pluginPermissions) {
                Bukkit.getPluginManager().removePermission(pluginPermission)
            }
        }

        @JvmStatic
        fun onEnable() {
            updateCommands()
        }

        @JvmStatic
        fun onDisable() {
            clearPermissions()
            unregisterAll()
        }

        fun updateCommands() {
            try {
                Bukkit.getServer().apply {
                    javaClass.getDeclaredMethod("syncCommands").invoke(this)
                }
            } catch (_: Exception) {
            }
        }
    }

}

fun commandRoot(
    /**
     * 节点名称
     */
    name: String,
    /**
     * 节点别名
     */
    alias: Array<String>? = null,
    /**
     * 默认权限
     */
    default: PermissionDefault = PermissionDefault.TRUE,
    /**
     * 描述
     */
    description: String? = null,
    /**
     * 是否异步运行
     */
    async: Boolean = false,
    /**
     * 参数列表
     */
    params: Array<Param> = emptyArray(),
    /**
     * 是否仅允许玩家执行
     */
    isPlayerOnly: Boolean = false,
    /**
     * 命令执行
     */
    onScope: (CommandBuilder.() -> Unit)? = null
): CommandBuilder {
    val commandNode = CommandNode(name, alias, description, default, async, params, isPlayerOnly)
    val commandBuilder = CommandBuilder(commandNode)
    onScope?.invoke(commandBuilder)
    if (commandNode.parent == null)
        CommandBuilder.register(commandNode)
    return commandBuilder
}