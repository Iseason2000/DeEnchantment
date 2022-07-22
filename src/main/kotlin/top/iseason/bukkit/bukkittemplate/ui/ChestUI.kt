package top.iseason.bukkit.bukkittemplate.ui

import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory

/**
 * 箱子界面
 * @param title 标题
 * @param row 行数
 */
open class ChestUI(
    var title: String = "Chest UI",
    row: Int = 6,
    override var clickDelay: Long = 200L
) : BaseUI(row * 9) {
//    override var baseInventory: Inventory = Bukkit.createInventory(this, row * 9, title)

    override fun reset() {
        resetSlots()
    }

    override fun buildInventory(): Inventory {
        baseInventory = Bukkit.createInventory(this, super.size, title)
        return baseInventory!!
    }

}