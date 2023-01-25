package top.iseason.bukkittemplate.ui.slot

import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import top.iseason.bukkittemplate.ui.container.BaseUI
import top.iseason.bukkittemplate.ui.container.UIContainer

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

/**
 * 获取slot的ui
 */
fun <T : BaseSlot> T.getUI(): BaseUI = baseInventory!!.holder as BaseUI

/**
 * 获取slot的container
 */
fun <T : BaseSlot> T.getContainer(): UIContainer? = getUI().container

