@file:Suppress("UNUSED", "MemberVisibilityCanBePrivate")

package top.iseason.bukkittemplate.utils.bukkit

import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.title.Title
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import top.iseason.bukkittemplate.BukkitTemplate
import top.iseason.bukkittemplate.DisableHook
import top.iseason.bukkittemplate.debug.warn
import top.iseason.bukkittemplate.hook.BungeeCordHook
import top.iseason.bukkittemplate.hook.PlaceHolderHook
import top.iseason.bukkittemplate.utils.other.submit
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * bukkit的消息相关工具
 */
object MessageUtils {
    /**
     * 消息处理队列，最后一个是发送消息给玩家的
     */
    val messageHandlers = LinkedList<MessageConsumer>()
    private var miniMessageSupport = false
    private var miniMessageLoaded = false
    lateinit var audiences: BukkitAudiences
    private val HEX_PATTERN = Pattern.compile("#([a-fA-F0-9]{6}|[a-fA-F0-9]{3})")
    private val hexColorSupport = try {
        net.md_5.bungee.api.ChatColor.of("#66ccff")
        true
    } catch (e: Throwable) {
        false
    }

    /**
     * 初始化默认消息解析器
     */
    init {
        //广播
        messageHandlers.add { msg, _, prefix ->
            if (msg.startsWith("[broadcast]", true)) {
                broadcast(msg.drop(11), prefix)
                return@add false
            }
            true
        }
        // actionbar
        messageHandlers.add { msg, sender, prefix ->
            if (sender is Player && msg.startsWith("[actionbar]", true)) {
                sender.sendActionBar(msg.drop(11), prefix)
                return@add false
            }
            true
        }
        //大标题
        messageHandlers.add { msg, sender, prefix ->
            if (sender is Player && msg.startsWith("[main-title]", true)) {
                sender.sendMainTitle(msg.drop(12), prefix)
                return@add false
            }
            true
        }
        //小标题
        messageHandlers.add { msg, sender, prefix ->
            if (sender is Player && msg.startsWith("[sub-title]", true)) {
                sender.sendSubTitle(msg.drop(11), prefix)
                return@add false
            }
            true
        }
        //命令
        messageHandlers.add { msg, sender, _ ->
            if (msg.startsWith("[command]", true) ||
                msg.startsWith("[console]", true) ||
                msg.startsWith("[op-command]", true)
            ) {
                val opCommand = msg.startsWith("[op-command]", true)
                val command =
                    PlaceHolderHook.setPlaceHolder(msg.drop(if (opCommand) 12 else 9), sender as? OfflinePlayer).trim()
                val realSender = if (msg.startsWith("[console]", true)) Bukkit.getConsoleSender() else sender
                val tempOP = opCommand && !realSender.isOp
                if (Bukkit.isPrimaryThread()) {
                    try {
                        if (tempOP) realSender.isOp = true
                        Bukkit.dispatchCommand(realSender, command)
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    } finally {
                        if (tempOP) realSender.isOp = false
                    }
                } else {
                    submit {
                        try {
                            if (tempOP) realSender.isOp = true
                            Bukkit.dispatchCommand(realSender, command)
                        } catch (e: Throwable) {
                            e.printStackTrace()
                        } finally {
                            if (tempOP) realSender.isOp = false
                        }
                    }
                }
                return@add false
            } else true
        }
        // 发送消息
        messageHandlers.add { msg, sender, prefix ->
            sender.sendMsg(PlaceHolderHook.setPlaceHolder("$prefix$msg", sender as? OfflinePlayer))
            false
        }
    }

    fun enableMiniMessage() {
        if (miniMessageSupport) return
        miniMessageSupport = true
        if (miniMessageLoaded) return
        val dd = BukkitTemplate.getRuntimeManager()
            .addRepository("https://maven.aliyun.com/repository/public")
            .addRepository("https://repo.maven.apache.org/maven2/")
        dd.addDependency("net.kyori:adventure-platform-bukkit:4.3.0", 4)
        dd.addDependency("net.kyori:adventure-text-minimessage:4.13.0", 1)
        dd.downloadAll()
        audiences = BukkitAudiences.create(BukkitTemplate.getPlugin())
        miniMessageLoaded = true
        DisableHook.addTask {
            audiences.close()
        }
    }

    fun disableMiniMessage() {
        miniMessageSupport = false
    }


    /**
     * 默认消息前缀
     */
    var defaultPrefix = "&a[&6${BukkitTemplate.getPlugin().description.name}&a] &f"

    /**
     * 颜色代码正则
     */
    val colorPattern: Pattern = Pattern.compile("#[A-F|\\d]{6}", Pattern.CASE_INSENSITIVE)

    /**
     * 将String转为bukkit支持的颜色消息,从 bentobox 抄来的
     * 例子: &a你好、#66ccff 你好、#6cf 你好
     */
    fun String.toColor(): String {
//        if (miniMessageSupport) return this
        if (!hexColorSupport) return ChatColor.translateAlternateColorCodes('&', this)
        val matcher: Matcher = HEX_PATTERN.matcher(this)
        // Increase buffer size by 32 like it is in bungee cord api. Use buffer because it is sync.
        val buffer = StringBuffer(length + 32)
        while (matcher.find()) {
            val group: String = matcher.group(1)
            if (group.length == 6) {
                // Parses #ffffff to a color text.
                matcher.appendReplacement(
                    buffer, ChatColor.COLOR_CHAR + "x"
                            + ChatColor.COLOR_CHAR + group[0] + ChatColor.COLOR_CHAR + group[1]
                            + ChatColor.COLOR_CHAR + group[2] + ChatColor.COLOR_CHAR + group[3]
                            + ChatColor.COLOR_CHAR + group[4] + ChatColor.COLOR_CHAR + group[5]
                )
            } else {
                // Parses #fff to a color text.
                matcher.appendReplacement(
                    buffer, ChatColor.COLOR_CHAR + "x"
                            + ChatColor.COLOR_CHAR + group[0] + ChatColor.COLOR_CHAR + group[0]
                            + ChatColor.COLOR_CHAR + group[1] + ChatColor.COLOR_CHAR + group[1]
                            + ChatColor.COLOR_CHAR + group[2] + ChatColor.COLOR_CHAR + group[2]
                )
            }
        }
        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString())
    }

    /**
     * String集合格式化颜色
     */
    fun Collection<String>.toColor() = map { it.toColor() }

    /**
     * 发送带颜色转换的消息,不输出null与空string
     */
    fun CommandSender.sendColorMessage(message: Any?, prefix: String = defaultPrefix) {
        //格式化消息,处理papi
        val messageList = if (message is Collection<*>) {
            message.mapNotNull { it?.toString() }
        } else if (message?.toString().isNullOrEmpty()) return
        else message!!.toString().split("\n")
        if (messageList.isEmpty()) return
        //是否传递消息,为了引用传递
        //每个消息都由消费者消费
        for (msg in messageList) {
            if (msg.isEmpty()) continue
            for (messageHandler in messageHandlers) {
                try {
                    if (!messageHandler.onHandler(msg, this, prefix)) {
                        break
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * 发送MiniMessage或者普通消息
     */
    private fun CommandSender.sendMsg(msg: String) {
        if (miniMessageSupport)
            audiences.sender(this).sendMessage(MiniMessage.miniMessage().deserialize(msg.noColor()))
        else sendMessage(msg)
    }

    /**
     * 发送带颜色转换的消息
     */
    fun CommandSender.sendColorMessages(vararg messages: Any?, prefix: String = defaultPrefix) =
        messages.forEach { sendColorMessage(it, prefix) }

    /**
     * 发送带颜色转换的消息
     */
    fun CommandSender.sendColorMessages(messages: Collection<Any?>?, prefix: String = defaultPrefix) =
        messages?.forEach { sendColorMessage(it, prefix) }

    /**
     * 发送带有前缀的消息,没有格式化颜色代码
     */
    fun CommandSender.sendMessage(message: Any?, prefix: String = defaultPrefix) {
        if (message == null || message.toString().isEmpty()) return
        sendMessage(prefix + message.toString())
    }

    /**
     * 进行颜色转换并发送给所有人
     */
    fun broadcast(message: Any?, prefix: String = defaultPrefix) {
        if (message == null || message.toString().isEmpty()) return
        val finalMessage = PlaceHolderHook.setPlaceHolder("$prefix$message", null)
        if (BungeeCordHook.bungeeCordEnabled) {
            if (miniMessageSupport) {
                val component = MiniMessage.miniMessage().deserialize(finalMessage)
                val serialize = GsonComponentSerializer.gson().serialize(component)
                BungeeCordHook.broadcastRaw(serialize)
            } else {
                BungeeCordHook.broadcast(finalMessage)
            }
        } else {
            if (miniMessageSupport) {
                val component = MiniMessage.miniMessage().deserialize(finalMessage)
                audiences.all().sendMessage(component)
            } else {
                Bukkit.broadcastMessage(finalMessage)
            }
        }
    }

    /**
     * 进行颜色转换并发送给控制台
     */
    fun sendConsole(message: Any?, prefix: String = defaultPrefix) =
        Bukkit.getConsoleSender().sendColorMessage(message, prefix)

    /**
     * 进行颜色转换并发送给控制台
     */
    fun sendConsole(messages: Collection<Any?>?, prefix: String = defaultPrefix) =
        messages?.forEach { sendConsole(it, prefix) }

    /**
     * 进行颜色转换并发送给控制台
     */
    fun sendConsole(messages: Array<Any?>?, prefix: String = defaultPrefix) =
        messages?.forEach { sendConsole(it, prefix) }

    /**
     * 去除字符串里的bukkit颜色代码
     */
    fun String.noColor(): String = ChatColor.stripColor(this)!!

    /**
     * 快速格式化字符串
     *
     * 例子: 你好啊 {0},欢迎来到 {1}
     *
     * 传入:  "Iseason", "我的世界"
     *
     * 结果: 你好啊 Iseason,欢迎来到 我的世界
     */
    fun String.formatBy(vararg values: Any?): String {
        var temp = this
        values.forEachIndexed { index, any ->
            if (any == null) return@forEachIndexed
            temp = temp.replace("{$index}", any.toString())
        }
        return temp
    }

    /**
     * 发送 actionbar 消息
     */
    fun Player.sendActionBar(message: String?, prefix: String = defaultPrefix) {
        if (message == null || message.toString().isEmpty()) return
        val finalMessage = PlaceHolderHook.setPlaceHolder("$prefix$message", this)
        try {
            if (miniMessageSupport) {
                val component = MiniMessage.miniMessage().deserialize(finalMessage)
                audiences.player(this).sendActionBar(component)
            } else {
                this.spigot()
                    .sendMessage(ChatMessageType.ACTION_BAR, *TextComponent.fromLegacyText(finalMessage))
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            warn("该服务端版本不支持 ActionBar 消息!")
        }
    }

    /**
     * 发送 title 消息
     */
    fun Player.sendMainTitle(message: String?, prefix: String = defaultPrefix) {
        if (message == null || message.toString().isEmpty()) return
        val finalMessage = PlaceHolderHook.setPlaceHolder("$prefix$message", this)
        if (miniMessageSupport) {
            val component = MiniMessage.miniMessage().deserialize(finalMessage)
            audiences.player(this).showTitle(Title.title(component, Component.empty()))
        } else {
            this.sendTitle(finalMessage, "")
        }
    }

    /**
     * 发送 subtitle 消息
     */
    fun Player.sendSubTitle(message: String?, prefix: String = defaultPrefix) {
        if (message == null || message.toString().isEmpty()) return
        val finalMessage = PlaceHolderHook.setPlaceHolder("$prefix$message", this)
        if (miniMessageSupport) {
            val component = MiniMessage.miniMessage().deserialize(finalMessage)
            audiences.player(this).showTitle(Title.title(Component.empty(), component))
        } else {
            this.sendTitle("", finalMessage)
        }
    }

    /**
     * 消息消费者
     */
    fun interface MessageConsumer {
        /**
         * 处理消息
         * @param msg 原始消息
         * @param sender 接收发送者
         * @param prefix 消息前缀
         * @return 是否传递消息给其他消费者
         */
        fun onHandler(msg: String, sender: CommandSender, prefix: String): Boolean
    }
}

