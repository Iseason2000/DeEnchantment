package top.iseason.bukkittemplate.hook

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.plugin.messaging.PluginMessageListener
import top.iseason.bukkittemplate.BukkitTemplate
import top.iseason.bukkittemplate.DisableHook
import top.iseason.bukkittemplate.debug.debug
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream


object BungeeCordHook : Listener {

    private const val BUNGEE_CORD_CHANNEL = "BungeeCord"

    @Volatile
    var bungeeCordEnabled = false
        private set

    private val bcListener: PluginMessageListener =
        PluginMessageListener { channel: String, _: Player?, _: ByteArray? ->
            if (bungeeCordEnabled) return@PluginMessageListener
            if (channel != BUNGEE_CORD_CHANNEL) {
                return@PluginMessageListener
            }
            debug("BungeeCord mode was enabled!")
            bungeeCordEnabled = true
        }

    init {
        Bukkit.getScheduler().runTaskAsynchronously(BukkitTemplate.getPlugin(), Runnable {
            Bukkit.getMessenger().registerOutgoingPluginChannel(BukkitTemplate.getPlugin(), "BungeeCord")
            registerListener(bcListener)
            val player = Bukkit.getOnlinePlayers().firstOrNull()
            if (player != null) check(player)
            Bukkit.getPluginManager().registerEvents(this, BukkitTemplate.getPlugin())
        })
        DisableHook.addTask(this::onDisable)
    }

    /**
     * 通过请求玩家IP来确认 bungeeCord 是否存在
     */
    private fun check(player: Player) {
        val stream = ByteArrayOutputStream()
        val out = DataOutputStream(stream)
        out.writeUTF("IP")
        try {
            player.sendPluginMessage(BukkitTemplate.getPlugin(), BUNGEE_CORD_CHANNEL, stream.toByteArray())
        } catch (_: Exception) {
            bungeeCordEnabled = false
        }
    }

    @JvmStatic
    fun onDisable() {
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(BukkitTemplate.getPlugin(), BUNGEE_CORD_CHANNEL)
        Bukkit.getMessenger().unregisterIncomingPluginChannel(BukkitTemplate.getPlugin(), BUNGEE_CORD_CHANNEL)
    }

    /**
     * 注册 BungeeCord 消息通道
     */
    fun registerListener(listener: PluginMessageListener) {
        Bukkit.getMessenger().registerIncomingPluginChannel(BukkitTemplate.getPlugin(), BUNGEE_CORD_CHANNEL, listener)
    }

    /**
     * 广播消息
     */
    fun broadcast(message: String) {
        broadcast(message, "Message")
    }

    /**
     * 广播 Raw消息
     */
    fun broadcastRaw(message: String) {
        broadcast(message, "MessageRaw")
    }

    private fun broadcast(message: String, type: String) {
        val stream = ByteArrayOutputStream()
        val out = DataOutputStream(stream)
        out.writeUTF(type)
        out.writeUTF("ALL")
        out.writeUTF(message)

        try {
            Bukkit.getOnlinePlayers().randomOrNull()
                ?.sendPluginMessage(BukkitTemplate.getPlugin(), BUNGEE_CORD_CHANNEL, stream.toByteArray())
        } catch (e: Exception) {
            e.printStackTrace()
            bungeeCordEnabled = false
            debug("BungeeCord mode was disabled!")
        }
    }

    /**
     * 检查bungee活跃
     */
    @EventHandler
    fun onPlayerLogin(event: PlayerLoginEvent) {
        check(event.player)
    }

}