package top.iseason.bukkit.deenchantment.settings

import top.iseason.bukkittemplate.config.Lang
import top.iseason.bukkittemplate.config.annotations.FilePath
import top.iseason.bukkittemplate.config.annotations.Key


@Key
@FilePath("message.yml")
object Message : Lang() {

    var anvil_cost = "&a本次附魔花费:&6 {0}"
    var enchant = "&6你的附魔发生了某些变化!"

    var command__give = "&a已给予&6 {0} &a负魔书 &6{1} "
    var command__random_book = "&a已给予&6 {0} &a负魔书 &6{1} "
    var command__random_enchant = "&a已添加&6 {0} &a负魔 &6{1} "
    var command__add_hand = "&c请拿着有效的物品!"
    var command__add = "&a负魔 &6{0} &a已添加!"
    var command__update_hand = "&c请拿着有效的物品!"
    var command__update = "&alore已更新!"
    var command__reload_success = "&a负魔已重新注册!"
    var command__reload_failure = "&c负魔注册异常，请反馈作者!"

    var command__purification_success = "&a你手中的物品已净化!"

    var command__migrate = "&a迁移完成!"
    var command__migrating = "&6数据迁移中,预计20秒......"

}