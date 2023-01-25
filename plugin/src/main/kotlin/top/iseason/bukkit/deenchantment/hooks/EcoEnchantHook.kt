package top.iseason.bukkit.deenchantment.hooks

import com.willfp.ecoenchants.EcoEnchantsPlugin
import com.willfp.ecoenchants.config.VanillaEnchantsYml
import com.willfp.ecoenchants.display.DisplayCache
import com.willfp.ecoenchants.enchants.registerVanillaEnchants
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkittemplate.debug.info
import top.iseason.bukkittemplate.debug.warn
import top.iseason.bukkittemplate.hook.BaseHook
import top.iseason.bukkittemplate.utils.other.submit
import java.io.File

object EcoEnchantHook : BaseHook("EcoEnchants") {

    var lang: VanillaEnchantsYml? = null
    var file: File? = null
    var hasHook = false

    fun init() {
        if (!hasHook) return
        lang = EcoEnchantsPlugin.instance.vanillaEnchantsYml
        info("&a检测到&6 EcoEnchant")
        hasHook = true
        try {
            DisplayCache.onReload()
        } catch (_: Exception) {
            warn("EcoEnchants 兼容异常，将在5秒后尝试重新更新!")
            submit(async = true, delay = 100) {
                warn("尝试兼容 EcoEnchants... ")
                DisplayCache.onReload()
                lang?.save()
                info("&a已兼容EcoEnchants!")
            }
        } finally {
            lang?.save()
        }

    }

    fun setInfo(enchant: BaseEnchant) {
        if (!hasHook) return
        lang?.set("${enchant.enchant.key.key}.name", enchant.translate_name)
        lang?.set("${enchant.enchant.key.key}.description", enchant.description)
    }

    fun save() {
        if (!hasHook) return
        try {
            lang?.save()
            registerVanillaEnchants(EcoEnchantsPlugin.instance)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

}