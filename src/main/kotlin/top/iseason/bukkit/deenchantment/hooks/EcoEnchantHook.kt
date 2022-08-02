package top.iseason.bukkit.deenchantment.hooks

import com.willfp.eco.core.config.base.LangYml
import com.willfp.ecoenchants.EcoEnchantsPlugin
import com.willfp.ecoenchants.display.EnchantmentCache
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import top.iseason.bukkit.bukkittemplate.debug.info
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import java.io.File

object EcoEnchantHook {
    var plugin: JavaPlugin? = Bukkit.getPluginManager().getPlugin("EcoEnchants") as JavaPlugin?
    var lang: LangYml? = null
    var file: File? = null
    var hasHook = false

    init {
        if (plugin != null) {
            lang = EcoEnchantsPlugin.getInstance().langYml
            info("&a检测到&6 EcoEnchant")
            hasHook = true
        }
    }

    fun setInfo(enchant: BaseEnchant) {
        lang?.set("enchantments.${enchant.enchant.key.key}.name", enchant.translate_name)
        lang?.set("enchantments.${enchant.enchant.key.key}.description", enchant.description)
//        lang?.save()
    }

    fun update() {
        if (plugin == null) return
        EnchantmentCache.update()

    }

}