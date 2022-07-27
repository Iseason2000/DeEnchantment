package top.iseason.bukkit.deenchantment.events

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * 玩家装备变化事件，不可取消
 */
class DePlayerEquipmentChangeEvent(
    val player: Player,
    val armors: Array<ItemStack?>
) :
    DeEnchantmentEvent(player, true)