package top.iseason.bukkittemplate.hook

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.OfflinePlayer
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.toColor

object PlaceHolderHook : BaseHook("PlaceholderAPI") {

    fun setPlaceHolder(str: String, player: OfflinePlayer?): String {
        return if (hasHooked)
            PlaceholderAPI.setPlaceholders(player, str).toColor()
        else str.toColor()
    }
}