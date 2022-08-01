package top.iseason.bukkit.deenchantment.settings

import org.bukkit.ChatColor
import top.iseason.bukkit.bukkittemplate.config.SimpleYAMLConfig
import top.iseason.bukkit.bukkittemplate.config.annotations.Comment
import top.iseason.bukkit.bukkittemplate.config.annotations.FilePath
import top.iseason.bukkit.bukkittemplate.config.annotations.Key
import top.iseason.bukkit.bukkittemplate.debug.info
import top.iseason.bukkit.deenchantment.listeners.enchantments.Frost_Walker
import top.iseason.bukkit.deenchantment.manager.DeEnchantments
import top.iseason.bukkit.deenchantment.manager.ListenerManager
import top.iseason.bukkit.deenchantment.runnables.TargetFinder
import java.lang.Thread.sleep

@FilePath("config.yml")
object Config : SimpleYAMLConfig(isAutoUpdate = false) {
    @Key
    @Comment("", "是否能够在铁砧中使用")
    var anvil: Boolean = true

    @Key
    @Comment("", "是否应用于砂轮祛魔")
    var grindstone: Boolean = true

    @Key
    @Comment("", "是否可以通过附魔台获得")
    var enchant: Boolean = true

    @Key
    @Comment("", "是否在有战利品的箱子中出现")
    var chestLoot: Boolean = true

    @Key
    @Comment("", "是否应用于自然生成的怪物身上")
    var spawn: Boolean = true

    @Key
    @Comment("", "是否应用于交易")
    var trade: Boolean = true

    @Key
    @Comment("", "是否应用于钓鱼获取")
    var fishing: Boolean = true

    @Key
    @Comment("", "是否应用于生物给予（猪灵交易、村庄英雄等生物丢物品行为）")
    var reward: Boolean = true

    @Key
    @Comment("", "是否突破等级上限")
    var levelUnlimited: Boolean = false

    @Key
    @Comment("", "是否允许铁砧过于昂贵仍能附魔，如果开启突破等级上限建议开启")
    var tooExpensive: Boolean = false

    @Key
    @Comment("", "是否开启控制台精简模式：开启后不会出现 所有附魔的名字")
    var cleanConsole: Boolean = false

    @Key
    @Comment("", "是否显示负魔描述")
    var allowDescription: Boolean = false

    var isInit = false

    //重载配置
    fun reload() {
        load()
        if (isInit) {
            onDisable()
        }
        ListenerManager.registerListeners()
        //等待负魔全部初始化
        if (!isInit) {
            sleep(500)
        }
        isInit = true
        try {
            DeEnchantments.registerEnchantments()
        } catch (e: Exception) {
            e.printStackTrace()
            info("${ChatColor.RED}附魔注册异常!")
        }
//        info("${ChatColor.GREEN}铁砧经验公式为: ${ChatColor.YELLOW} $expression")

    }

    fun onDisable() {
        try {
            DeEnchantments.resetEnchantments()

        } catch (e: Exception) {
            e.printStackTrace()
            info("${ChatColor.RED}注销附魔异常!")
        }
        Frost_Walker.clear()
        TargetFinder.removeAll()
        ListenerManager.unRegisterAll()
    }


}