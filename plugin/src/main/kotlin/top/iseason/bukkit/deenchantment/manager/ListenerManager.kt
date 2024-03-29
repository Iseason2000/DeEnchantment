package top.iseason.bukkit.deenchantment.manager

import org.bukkit.ChatColor
import org.bukkit.event.HandlerList
import top.iseason.bukkit.deenchantment.DeEnchantment
import top.iseason.bukkit.deenchantment.hooks.EcoEnchantHook
import top.iseason.bukkit.deenchantment.listeners.BaseEnchant
import top.iseason.bukkit.deenchantment.listeners.controllers.*
import top.iseason.bukkit.deenchantment.listeners.enchantments.Aqua_Affinity
import top.iseason.bukkit.deenchantment.listeners.triggers.EntityDeEnchantCaller
import top.iseason.bukkit.deenchantment.settings.Config
import top.iseason.bukkit.deenchantment.utils.ClassGetter
import top.iseason.bukkittemplate.debug.info
import top.iseason.bukkittemplate.utils.bukkit.EventUtils.registerListener
import java.lang.reflect.Field

object ListenerManager {

    fun registerListeners() {
        registerControllers()
        registerEnchantments()
        EntityDeEnchantCaller.registerListener()
    }

    fun unRegisterAll() {
        HandlerList.unregisterAll(DeEnchantment.javaPlugin)
    }

    private fun registerEnchantments() {
        //自动注册附魔监听器
        val classGetter =
            ClassGetter(
                DeEnchantment.javaPlugin,
                Aqua_Affinity::class.java.name.replace("." + Aqua_Affinity::class.java.simpleName, "")
            )
        for (cl in classGetter.classes) {
            if (!BaseEnchant::class.java.isAssignableFrom(cl)) continue
            val instance: Field = try {
                cl.getDeclaredField("INSTANCE")
            } catch (e: Exception) {
                continue
            }
            instance.isAccessible = true
            val newInstance = instance.get(null) as BaseEnchant
            newInstance.load(false)
            if (newInstance.enable) {
                newInstance.registerListener()
            }
        }
    }

    private fun registerControllers() {
        val str = StringBuilder()
        if (Config.anvil && !EcoEnchantHook.hasHook) {
            str.append("铁砧、")
            AnvilListener.registerListener()
        }
        if (Config.chestLoot) {
            str.append("箱子、")
            ChestLootTableListener.registerListener()
        }
        if (Config.enchant) {
            str.append("附魔台、")
            EnchantListener.registerListener()
        }
        if (Config.spawn) {
            str.append("生物、")
            EntitySpawnListener.registerListener()
        }
        if (Config.trade) {
            str.append("交易、")
            MerchantListener.registerListener()
        }
        if (Config.fishing) {
            str.append("钓鱼、")
            PlayerFishListener.registerListener()
        }
        if (Config.reward) {
            str.append("给予、")
            EntityDropItemListener.registerListener()
        }
        if (Config.grindstone) {
            str.append("砂轮")
            GrindstoneListener.registerListener()
        }
        info("${ChatColor.YELLOW}负魔应用于：${ChatColor.WHITE}$str")
    }
}