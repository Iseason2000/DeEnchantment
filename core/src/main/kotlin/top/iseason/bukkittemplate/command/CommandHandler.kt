package top.iseason.bukkittemplate.command

import org.bukkit.Bukkit
import org.bukkit.command.PluginCommand
import org.bukkit.command.SimpleCommandMap
import org.bukkit.permissions.Permission
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.SimplePluginManager
import top.iseason.bukkittemplate.BukkitTemplate
import top.iseason.bukkittemplate.DisableHook
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.toColor
import java.lang.reflect.Constructor
import java.util.*
import java.util.stream.Collectors

/**
 * 所有命令的管理类
 */
@Suppress("unused")
object CommandHandler {
    /**
     * 储存命令权限
     */
    private val commandPermissions = mutableSetOf<Permission>()

    /**
     * 所有注册的命令
     */
    private val registeredCommands = mutableListOf<PluginCommand>()
    private val simpleCommandMap: SimpleCommandMap
    private val pluginCommandConstructor: Constructor<PluginCommand> = getPluginCommandConstructor()

    init {
        val simplePluginManager = Bukkit.getServer().pluginManager as SimplePluginManager
        val commandMapField = SimplePluginManager::class.java.getDeclaredField("commandMap")
        commandMapField.isAccessible = true
        simpleCommandMap = commandMapField.get(simplePluginManager) as SimpleCommandMap
        DisableHook.addTask { onDisable() }
    }

    // PluginCommand原本只能通过plugin.yml中声明的命令创建，有点自私，乖乖开放吧
    private fun getPluginCommandConstructor(): Constructor<PluginCommand> {
        val constructor = PluginCommand::class.java.getDeclaredConstructor(String::class.java, Plugin::class.java)
        constructor.isAccessible = true
        return constructor
    }

    /**
     * 将命令节点注册为根命令
     */
    fun register(commandNode: CommandNode) {
        require(commandNode.parent == null) { "只能为根命令注册!" }
        val pluginCommand =
            pluginCommandConstructor.newInstance(commandNode.name, BukkitTemplate.getPlugin()) as PluginCommand
        if (commandNode.alias != null) {
            pluginCommand.aliases = Arrays.stream(commandNode.alias).collect(Collectors.toList())
        }
        if (commandNode.description != null) {
            pluginCommand.description = commandNode.description!!
        }
        pluginCommand.permissionMessage = commandNode.noPermissionMessage?.toColor()
        pluginCommand.setExecutor(commandNode)
        pluginCommand.tabCompleter = commandNode
        simpleCommandMap.register(BukkitTemplate.getPlugin().name, pluginCommand)
        commandPermissions.add(commandNode.permission)
        registeredCommands.add(pluginCommand)
    }

    // 注销根命令
    fun unregister(commandNode: CommandNode) {
        require(commandNode.parent == null) { "非根命令不需要注销!" }
        Bukkit.getServer().getPluginCommand(commandNode.name)?.unregister(simpleCommandMap)
    }

    /**
     * 注销所有已经注册的插件
     */
    @JvmStatic
    fun unregisterAll() {
        for (registeredCommand in registeredCommands) {
            registeredCommand.unregister(simpleCommandMap)
        }
    }

    /**
     * 添加一个命令权限进入缓存，用于注销
     */
    fun addPermissions(perm: Permission) {
        commandPermissions.add(perm)
    }

    /**
     * 清除所有命令的权限
     */
    fun clearPermissions() {
        for (pluginPermission in commandPermissions) {
            Bukkit.getPluginManager().removePermission(pluginPermission)
        }
    }

    /**
     * 正确地注销所有命令
     */
    @JvmStatic
    fun onDisable() {
        clearPermissions()
        unregisterAll()
    }

    /**
     * 更新注册的命令以支持tab
     */
    @JvmStatic
    fun updateCommands() {
        try {
            Bukkit.getServer().apply {
                javaClass.getDeclaredMethod("syncCommands").invoke(this)
            }
        } catch (_: Exception) {
        }
    }

}

/**
 * 创建命令根节点
 * @param name 名字
 * @param modify 对节点的修改
 * @return 创建的节点
 */
fun command(name: String, modify: (CommandNode.() -> Unit)? = null): CommandNode {
    val commandNode = node(name, modify)
    CommandHandler.register(commandNode)
    return commandNode
}

/**
 * 创建命令根节点
 * @param name 名字
 * @param modify 对节点的修改
 * @return 创建的节点
 */
fun command(node: CommandNode, modify: (CommandNode.() -> Unit)? = null): CommandNode {
    modify?.invoke(node)
    CommandHandler.register(node)
    return node
}

/**
 * 为节点添加子节点
 * @param name 名字
 * @param modify 对节点的修改
 * @return 创建的节点
 */
fun CommandNode.node(name: String, modify: (CommandNode.() -> Unit)? = null): CommandNode {
    val commandNode = top.iseason.bukkittemplate.command.node(name, modify)
    this.addSubNode(commandNode)
    return commandNode
}

/**
 * 创建一个没有上下关系的节点
 * 使用CommandNode::addSubNode添加子节点或将自己添加到某个父节点中
 *
 * @param name 名字
 * @param modify 对节点的修改
 * @return 创建的节点
 */
fun node(name: String, modify: (CommandNode.() -> Unit)? = null): CommandNode {
    val commandNode = CommandNode(name)
    modify?.let { it(commandNode) }
    return commandNode
}

/**
 * 为节点添加子节点
 * @param name 名字
 * @param modify 对节点的修改
 * @return 创建的节点
 */
fun CommandNode.node(node: CommandNode): CommandNode {
    this.addSubNode(node)
    return node
}

/**
 * 设置命令执行者
 */
fun CommandNode.executor(onExecute: CommandNodeExecutor? = null) {
    this.onExecute = onExecute
}

/**
 * 添加一个参数
 */
fun CommandNode.param(
    placeholder: String,
    suggest: Collection<String>? = null,
    suggestRuntime: Param.RuntimeSuggestParams? = null
) {
    params = params.toMutableList().apply { add(Param(placeholder, suggest, suggestRuntime)) }
}
