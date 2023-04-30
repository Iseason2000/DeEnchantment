package top.iseason.bukkittemplate.command

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault
import top.iseason.bukkittemplate.BukkitTemplate
import top.iseason.bukkittemplate.debug.SimpleLogger
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.sendColorMessage
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.sendColorMessages
import top.iseason.bukkittemplate.utils.other.submit
import java.util.*

@Suppress("MemberVisibilityCanBePrivate", "unused")
open class CommandNode(
    /**
     * 节点名称
     */
    val name: String,
    /**
     * 节点别名
     */
    var alias: Array<String>? = null,
    /**
     * 描述
     */
    var description: String? = null,
    /**
     * 默认执行权限
     */
    var default: PermissionDefault = PermissionDefault.TRUE,
    /**
     * 是否异步执行
     */
    var async: Boolean = false,
    /**
     * 参数列表
     */
    var params: List<Param> = listOf(),
    /**
     * 是否仅玩家执行
     */
    var isPlayerOnly: Boolean = false,
    /**
     * 命令执行
     */
    open var onExecute: CommandNodeExecutor? = null
) : CommandExecutor, TabExecutor {
    var permission: Permission =
        Permission("${BukkitTemplate.getPlugin().name.lowercase()}.$name", default)

    /**
     * 获取父节点
     */
    var parent: CommandNode? = null
        private set(value) {
            field = value
            if (field != null) {
                val str = "${field!!.permission.name}.$name".lowercase(Locale.ENGLISH)
                val par = "${field!!.permission.name}.*".lowercase(Locale.ENGLISH)
                var parentPerm = Bukkit.getPluginManager().getPermission(par)
                if (parentPerm == null) {
                    parentPerm = Permission(par, PermissionDefault.OP)
                    Bukkit.getPluginManager().addPermission(parentPerm)
                    CommandHandler.addPermissions(parentPerm)
                }
                permission = Permission(str, default)
                try {
                    Bukkit.getPluginManager().addPermission(permission)
                } catch (e: Exception) {
                    permission = Bukkit.getPluginManager().getPermission(permission.name)!!
                }
                permission.addParent(parentPerm, true)
            }
        }

    /**
     * 子节点名称集合
     */
    val subNodeKeys = mutableListOf<String>()

    /**
     * 子节点映射，全小写
     */
    val subNodesInsensitive = mutableMapOf<String, CommandNode>()

    /**
     * 参数类型和建议参数
     */
    var noPermissionMessage: String? = CommandNode.noPermissionMessage

    /**
     * 添加子节点
     */
    fun addSubNode(node: CommandNode) {
        if (parent == null && Bukkit.getPluginManager().getPermission(permission.name) == null) {
            Bukkit.getPluginManager().addPermission(permission)
        }
        subNodeKeys += node.name
        subNodesInsensitive[node.name.lowercase()] = node
        node.parent = this
        CommandHandler.addPermissions(node.permission)
        node.alias?.forEach {
            subNodeKeys += it
            subNodesInsensitive[it.lowercase()] = node
        }
    }

    /**
     * 获取子节点,大小写不敏感
     * @return null if not exists
     */
    fun getSubNode(arg: String) = subNodesInsensitive[arg.lowercase()]

    /**
     * 获取该命令发送者可见的子节点
     * @return null if not exists
     */
    fun getSubNode(arg: String, sender: CommandSender): CommandNode? {
        val commandNode = getSubNode(arg) ?: return null
        if (!commandNode.canUse(sender)) return null
        return commandNode
    }

    /**
     * 获取命令执行者可见的所有子节点
     */
    fun getSubNodes(sender: CommandSender): Set<CommandNode> {
        val set = mutableSetOf<CommandNode>()
        for (value in subNodesInsensitive.values) {
            if (!value.canUse(sender)) continue
            set.add(value)
        }
        return set
    }

    /**
     * 获取根节点
     */
    fun getRootNode(): CommandNode = parent?.getRootNode() ?: this

    /**
     * 判断命令执行者是否可用
     */
    fun canUse(sender: CommandSender): Boolean {
        return sender.hasPermission(permission)
    }

    /**
     * 获取用户有权限的子键名称
     */
    fun getKeys(sender: CommandSender): MutableList<String> {
        return subNodeKeys.filter {
            getSubNode(it)?.canUse(sender) == true
        }.toMutableList()

    }

    /**
     * 在命令执行者按下Tab时调用，只调用根节点
     */
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): List<String>? {
        require(parent == null) { "只有根节点才能使用" }
        var node: CommandNode = this
        // 不完整参数
        var incomplete = ""
        var deep = 0
        for ((index, arg) in args.withIndex()) {
            val subNode = if (arg.isBlank()) null else node.getSubNode(arg, sender)
            if (subNode == null) {
                incomplete = arg
                deep = index
                break
            }
            node = subNode
        }
        val keys = node.getKeys(sender)
        if (keys.isEmpty() && node.params.isNotEmpty()) {
            val last = args.last()
            val param = node.params.getOrNull(args.size - deep - 1) ?: return null
            return (param.suggestRuntime?.getParams(sender) ?: param.suggest)?.filter {
                it.startsWith(last, true)
            }
        }
        return keys.filter { it.startsWith(incomplete, true) }
    }

    /**
     * 执行命令，由根节点负责转发，最终调用 onExecute 方法
     */
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        require(parent == null) { "只有根节点才能使用" }
        if (!canUse(sender)) {
            sender.sendColorMessage(noPermissionMessage?.replace("%permission%", permission.name))
            return true
        }
        var node: CommandNode = this
        var deep = 0
        for (arg in args) {
            node = node.getSubNode(arg)?.apply {
                if (!canUse(sender)) {
                    sender.sendColorMessage(
                        noPermissionMessage?.replace("%permission%", permission.name),
                        SimpleLogger.prefix
                    )
                    return true
                }
            } ?: break
            deep++
        }
        if (node.subNodeKeys.isNotEmpty() && node.onExecute == null) {
            node.showUsage(sender)
            return true
        }
        val params = args.copyOfRange(deep, args.size)
        if (node.onExecute == null) return true
        if (node.isPlayerOnly && sender !is Player) {
            sender.sendColorMessage("&c该命令仅限制玩家使用!")
            return true
        }
        submit(async = node.async) {
            try {
                node.onExecute!!.accept(Params(params, node), sender)
            } catch (e: ParmaException) {
                //参数错误的提示
                if (e.paramAdopter != null) {
                    sender.sendColorMessage(e.paramAdopter.errorMessage.format(e.arg))
                } else {
                    val message = e.message ?: return@submit
                    sender.sendColorMessage(message)
                }
            }
        }
        return true
    }

    /**
     * 展示用法
     */
    fun showUsage(sender: CommandSender) {
        val list = mutableListOf<String>()
        if (usageHeader != null) list.add(usageHeader!!)
        val subs = getSubNodes(sender)
        if (subs.isEmpty() && params.isEmpty()) return
        for (key in subs) {
            list.add(usage.format(key.name, key.getSuggest(), key.description ?: ""))
        }
        if (subs.isEmpty() && params.isNotEmpty()) {
            list.add(usage.format(getWholeCommand(), getSuggest(), description ?: ""))
        }
        if (usageFooter != null) list.add(usageFooter!!)
        sender.sendColorMessages(list)
    }

    /**
     * 注册节点
     */
    fun registerAsRoot() = CommandHandler.register(this)

    /**
     * 获取完整的命令结构
     */
    fun getWholeCommand(): String {
        var node = this
        var command = name
        while (node.parent != null) {
            command = "${node.parent!!.name} $command"
            node = node.parent!!
        }
        return command
    }

    /**
     * 获取命令的建议
     */
    fun getSuggest(): String {
        val sb = StringBuilder()
        for (pa in params) {
            sb.append(pa.placeholder).append(" ")
        }
        return sb.toString()
    }

    companion object {

        // 没有权限的消息
        var noPermissionMessage: String? = "&c你没有该命令的权限: &7%permission%"

        // 使用提示消息头
        var usageHeader: String? = "  &7=======> &d${BukkitTemplate.getPlugin().name} &7<======="

        /**
         * 命令用法
         */
        var usage: String = "&7 - &6%s &a%s &7%s"

        // 使用提示消息尾部
        var usageFooter: String? = " "
    }

}