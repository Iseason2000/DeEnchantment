package top.iseason.bukkit.bukkittemplate.ui

import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

/**
 * 一个物品槽
 */
interface BaseSlot {
    /**
     * 物品槽的位置
     */
    val index: Int

    /**
     * 存在的物品栏
     */
    var baseInventory: Inventory?


    /**
     * 存在的物品
     */
    var itemStack: ItemStack?

    /**
     * 复制Slot到指定Index
     */
    fun clone(index: Int): BaseSlot

    /**
     * 重置Slot
     */
    fun reset()
}