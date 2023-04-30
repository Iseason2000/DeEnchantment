package top.iseason.bukkittemplate.hook

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import top.iseason.bukkittemplate.BukkitTemplate
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.toColor

object PlaceHolderHook : BaseHook("PlaceholderAPI") {
    init {
        Bukkit.getScheduler().runTaskAsynchronously(BukkitTemplate.getPlugin(), PlaceHolderHook::checkHooked)
    }

    fun setPlaceHolder(str: String, player: OfflinePlayer?): String {
        return if (hasHooked)
            PlaceholderAPI.setPlaceholders(player, str).toColor()
        else str.toColor()
    }
}