package top.iseason.bukkit.bukkittemplate.command

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault
import top.iseason.bukkit.bukkittemplate.TemplatePlugin
import top.iseason.bukkit.bukkittemplate.debug.SimpleLogger
import top.iseason.bukkit.bukkittemplate.utils.WeakCoolDown
import top.iseason.bukkit.bukkittemplate.utils.sendColorMessage
import top.iseason.bukkit.bukkittemplate.utils.sendColorMessages
import top.iseason.bukkit.bukkittemplate.utils.submit
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
    val alias: Array<String>? = null,
    /**
     * 描述
     */
    val description: String? = null,
    /**
     * 默认执行权限
     */
    private val default: PermissionDefault = PermissionDefault.TRUE,
    /**
     * 是否异步执行
     */
    private val async: Boolean = false,
    /**
     * 参数列表
     */
    val params: Array<Param> = emptyArray(),
    /**
     * 是否仅玩家执行
     */
    val isPlayerOnly: Boolean = false,
    /**
     * 命令执行
     */
    var onExecute: (Params.(sender: CommandSender) -> Boolean)? = null
) : CommandExecutor, TabExecutor {
    var permission: Permission =
        Permission("${TemplatePlugin.getPlugin().name.lowercase()}.$name", default)

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
                    CommandBuilder.addPermissions(parentPerm)
                }
                permission = Permission(str, default)
                Bukkit.getPluginManager().addPermission(permission)
                permission.addParent(parentPerm, true)
            }
        }

    /**
     * 子节点
     */
    private val subNodes = mutableMapOf<String, CommandNode>()

    /**
     * 参数类型和建议参数
     */
    var successMessage: String? = CommandNode.successMessage
    var failureMessage: String? = CommandNode.failureMessage
    var noPermissionMessage: String? = CommandNode.noPermissionMessage

    /**
     * 添加子节点
     */
    fun addSubNode(node: CommandNode) {
        if (parent == null && Bukkit.getPluginManager().getPermission(permission.name) == null) {
            Bukkit.getPluginManager().addPermission(permission)
        }
        subNodes[node.name] = node
        node.parent = this
        node.alias?.forEach {
            subNodes[it] = node
        }
    }

    /**
     * @return null if not exists
     */
    private fun getSubNode(arg: String) = subNodes[arg]

    /**
     * 获取该命令发送者可见的子节点
     * @return null if not exists
     */
    private fun getSubNode(arg: String, sender: CommandSender): CommandNode? {
        val commandNode = getSubNode(arg) ?: return null
        if (!commandNode.canUse(sender)) return null
        return commandNode
    }

    /**
     * 获取命令执行者可见的子节点
     */
    private fun getSubNodes(sender: CommandSender): Set<CommandNode> {
        val set = mutableSetOf<CommandNode>()
        for (value in subNodes.values) {
            if (!value.canUse(sender)) continue
            set.add(value)
        }
        return set
    }

    /**
     * 获取根节点
     */
    private fun getRootNode(): CommandNode = parent?.getRootNode() ?: this

    /**
     * 判断命令执行者是否可用
     */
    private fun canUse(sender: CommandSender): Boolean {
        return sender.hasPermission(permission)
    }

    /**
     * 获取子健
     */
    private fun getKeys(sender: CommandSender): MutableList<String> {
        val mutableListOf = mutableListOf<String>()
        subNodes.forEach { (k, v) ->
            if (!v.canUse(sender)) return@forEach
            mutableListOf.add(k)
        }
        return mutableListOf
    }


    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): List<String>? {
        require(parent == null) { "只有根节点才能使用" }
        //200毫秒冷却，防止
        if (coolDown.check(sender, 200)) {
            return emptyList()
        }
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
//        println(node.name)
        val keys = node.getKeys(sender)
        if (keys.isEmpty() && node.params.isNotEmpty()) {
            val last = args.last()
            val param = node.params.getOrNull(args.size - deep - 1) ?: return null
            return (param.suggestRuntime?.invoke(sender) ?: param.suggest)?.filter {
                it.startsWith(last, true)
            }
        }
        return keys.filter { it.startsWith(incomplete, true) }
    }

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
        if (node.subNodes.isNotEmpty() && node.onExecute == null) {
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
                if (node.onExecute!!.invoke((Params(params, node)), sender)) {
                    if (node.successMessage != null)
                        sender.sendColorMessage(node.successMessage)
                } else if (node.failureMessage != null) sender.sendColorMessage(
                    node.failureMessage,
                    SimpleLogger.prefix
                )
            } catch (e: ParmaException) {
                //参数错误的提示
                if (e.typeParam != null) sender.sendColorMessage(e.typeParam.errorMessage(e.arg))
                else {
                    node.showUsage(sender)
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
    private fun showUsage(sender: CommandSender) {
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
    fun registerAsRoot() = CommandBuilder.register(this)

    /**
     * 获取整个命令
     */
    private fun getWholeCommand(): String {
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
    private fun getSuggest(): String {
        val sb = StringBuilder()
        for (pa in params) {
            sb.append(pa.placeholder).append(" ")
        }
        return sb.toString()
    }

    companion object {
        // 执行成功的消息
        var successMessage: String? = null

        // 执行失败的消息
        var failureMessage: String? = null

        // 没有权限的消息
        var noPermissionMessage: String? = "&c你没有该命令的权限: &7%permission%"

        // 使用提示消息头
        var usageHeader: String? = "  &7=======> &d${TemplatePlugin.getPlugin().name} &7<======="
        var usage: String = "&7 - &6%s &a%s &7%s"

        // 使用提示消息尾部
        var usageFooter: String? = " "
        val coolDown = WeakCoolDown<CommandSender>()
    }

}