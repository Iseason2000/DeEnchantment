package top.iseason.bukkit.deenchantment.events

import org.bukkit.entity.Player

/**
 * 玩家装备变化事件，不可取消
 */
class PlayerEquipmentChangeEvent(player: Player) : PlayerDeEnchantmentEvent(player)