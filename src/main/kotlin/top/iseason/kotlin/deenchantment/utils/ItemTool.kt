package top.iseason.kotlin.deenchantment.utils

import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

@Deprecated("没用过，但是是一种思路")
object ItemTool {
    fun getItemSlot(item: ItemStack): EquipmentSlot {
        val type = item.type.toString()
        return when {
            type.contains("HELMET") -> EquipmentSlot.HEAD
            type.contains("CHESTPLATE") -> EquipmentSlot.CHEST
            type.contains("LEGGINGS") -> EquipmentSlot.LEGS
            type.contains("BOOTS") -> EquipmentSlot.FEET
            else -> EquipmentSlot.OFF_HAND //其他非装备生效物品
        }
    }
}